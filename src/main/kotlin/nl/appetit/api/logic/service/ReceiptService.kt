package nl.appetit.api.logic.service

import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.PdfWriter
import nl.appetit.api.data.repository.TablePaymentOrderR2dbcRepository
import nl.appetit.api.data.repository.TablePaymentR2dbcRepository
import nl.appetit.api.logic.repository.OrderRepository
import nl.appetit.api.logic.service.OrderItemService
import nl.appetit.api.presentation.dto.order_item.OrderItemResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Service
class ReceiptService(
    private val tablePaymentRepository: TablePaymentR2dbcRepository,
    private val tablePaymentOrderRepository: TablePaymentOrderR2dbcRepository,
    private val orderRepository: OrderRepository,
    private val orderItemService: OrderItemService,
) {

    private val dateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault())

    private val localeNl: Locale = Locale("nl", "NL")
    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(localeNl)

    /**
     * Generate a simple restaurant-style receipt (bon) for a given table payment.
     *
     * Layout:
     *  - Narrow page (80mm bon-formaat)
     *  - Monospace font, vaste kolommen
     *  - Header, orderregels en totaal
     */
    fun generateReceiptForTablePayment(tablePaymentId: Int): Mono<ByteArray> {
        return tablePaymentRepository.findById(tablePaymentId)
            .switchIfEmpty(
                Mono.error(
                    nl.appetit.api.logic.exception.NotFoundException(
                        "Table payment $tablePaymentId not found",
                    ),
                ),
            )
            .flatMap { tablePayment ->
                tablePaymentOrderRepository.findByTablePaymentId(tablePayment.id!!)
                    .flatMap { link ->
                        orderRepository.findById(link.orderId)
                    }
                    .collectList()
                    .flatMap { orders ->
                        // Haal voor elke order de items op met productdata
                        if (orders.isEmpty()) {
                            Mono.just(
                                Pair(
                                    tablePayment,
                                    emptyList<Pair<Int, List<OrderItemResponse>>>(),
                                ),
                            )
                        } else {
                            // Haal items op voor alle orders en combineer
                            Flux.fromIterable(orders)
                                .flatMap { order ->
                                    orderItemService.getItemsByOrderWithProducts(order.id!!)
                                        .collectList()
                                        .map { items -> Pair(order.id!!, items) }
                                }
                                .collectList()
                                .map { itemsList -> Pair(tablePayment, itemsList) }
                        }
                    }
                    .map { (tablePayment, ordersWithItems) ->
                        val baos = ByteArrayOutputStream()

                        // 80mm receipt width ~ 226.77 points; height genoeg voor items
                        val pageSize = Rectangle(226.77f, 800f)
                        val document = Document(pageSize, 10f, 10f, 10f, 10f)
                        PdfWriter.getInstance(document, baos)
                        document.open()

                        val titleFont = Font(Font.COURIER, 11f, Font.BOLD)
                        val normalFont = Font(Font.COURIER, 9f, Font.NORMAL)

                        fun addLine(text: String = "", font: Font = normalFont) {
                            document.add(Paragraph(text, font))
                        }

                        fun lineSeparator() = "-".repeat(28)

                        // Header
                        addLine("APPETIT", titleFont)
                        addLine("Restaurant bon", normalFont)
                        addLine()

                        addLine("Bon: ${tablePayment.id}", normalFont)
                        addLine("Tafel: ${tablePayment.tableId ?: "-"}", normalFont)
                        addLine(
                            "Datum: ${
                                dateFormatter.format(
                                    tablePayment.createdAt.atZone(ZoneId.systemDefault()),
                                )
                            }",
                            normalFont,
                        )
                        addLine(lineSeparator(), normalFont)

                        // Kolomkop: QTY ITEM           TOTAAL
                        addLine("QTY  ITEM               TOTAAL", normalFont)
                        addLine(lineSeparator(), normalFont)

                        if (ordersWithItems.isEmpty()) {
                            addLine(" (geen orders)", normalFont)
                        } else {
                            // Toon alle items van alle orders
                            ordersWithItems.forEach { (orderId, items) ->
                                if (items.isNotEmpty()) {
                                    items.forEach { item ->
                                        val unitPrice =
                                            (item.price ?: BigDecimal.ZERO)
                                                .setScale(2, RoundingMode.HALF_UP)
                                        val itemTotal = unitPrice.multiply(BigDecimal(item.quantity))
                                            .setScale(2, RoundingMode.HALF_UP)
                                        val productName = item.product?.name ?: "Product #${item.productId ?: "-"}"
                                        val qtyStr = "${item.quantity}x"
                                        val totalStr = currencyFormat.format(itemTotal)

                                        val line = String.format(
                                            "%-3s %-15s %8s",
                                            qtyStr,
                                            productName.take(15),
                                            totalStr,
                                        )
                                        addLine(line, normalFont)
                                    }
                                }
                            }
                        }

                        addLine(lineSeparator(), normalFont)

                        // Total
                        val total = tablePayment.totalAmount
                            .setScale(2, RoundingMode.HALF_UP)
                        val totalStr = currencyFormat.format(total)
                        val totalLine = String.format("%-18s %8s", "TOTAAL:", totalStr)
                        addLine(totalLine, titleFont)

                        addLine()
                        addLine("Bedankt en tot ziens!", normalFont)

                        document.close()
                        baos.toByteArray()
                    }
            }
    }
}

