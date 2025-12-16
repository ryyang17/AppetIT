package nl.appetit.api.logic.service

import nl.appetit.api.data.entity.TablePaymentEntity
import nl.appetit.api.data.entity.TablePaymentOrderEntity
import nl.appetit.api.logic.repository.OrderRepository
import nl.appetit.api.data.repository.TablePaymentOrderR2dbcRepository
import nl.appetit.api.data.repository.TablePaymentR2dbcRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Instant

@Service
class PaymentService(
    private val orderRepository: OrderRepository,
    private val orderService: OrderService,
    private val tablePaymentRepository: TablePaymentR2dbcRepository,
    private val tablePaymentOrderRepository: TablePaymentOrderR2dbcRepository
) {

    private val logger = LoggerFactory.getLogger(PaymentService::class.java)

    /**
     * Create payments for all READY orders of a table and then mark them as COMPLETED.
     * This will create one Payment per Order so that the relation between payment and order
     * is always explicit and can be queried later.
     */
    fun createPaymentsForTable(
        tableId: Int,
        paymentMethod: String? = "UNKNOWN"
    ): Mono<Map<String, Any?>> {
        logger.info("Creating payments for READY orders on table {}", tableId)

        return orderRepository.findByTableIdAndStatus(tableId, "READY")
            .collectList()
            .flatMap { readyOrders ->
                if (readyOrders.isEmpty()) {
                    logger.info("No READY orders found for table {}", tableId)
                    return@flatMap Mono.just(
                        mapOf(
                            "tableId" to tableId,
                            "paymentCount" to 0,
                            "totalAmount" to BigDecimal.ZERO,
                            "message" to "No READY orders found for table"
                        )
                    )
                }

                val now = Instant.now()

                // First compute the total amount for all READY orders
                val totalAmount = readyOrders.fold(BigDecimal.ZERO) { acc, order ->
                    acc + (order.totalAmount ?: BigDecimal.ZERO)
                }

                // Create one TablePayment record for this table
                tablePaymentRepository.save(
                    TablePaymentEntity(
                        tableId = tableId,
                        totalAmount = totalAmount,
                        paymentMethod = paymentMethod,
                        status = "PAID",
                        createdAt = now
                    )
                ).flatMap { tablePayment ->
                    val tablePaymentId = tablePayment.id
                        ?: throw IllegalStateException("TablePayment without id cannot be persisted correctly")

                    // Create link records between this table payment and each order
                    val linksFlux: Flux<TablePaymentOrderEntity> =
                        Flux.fromIterable(readyOrders)
                            .flatMap { order ->
                                val orderId = order.id
                                    ?: throw IllegalStateException("Order without id can not be paid")
                                tablePaymentOrderRepository.save(
                                    TablePaymentOrderEntity(
                                        tablePaymentId = tablePaymentId,
                                        orderId = orderId
                                    )
                                )
                            }

                    linksFlux
                        .collectList()
                        .flatMap { links ->
                            // After creating the table payment + links, mark the orders as COMPLETED
                            orderService.completeOrdersForTable(tableId)
                                .map { completeResult ->
                                    logger.info(
                                        "Created table payment {} for table {} with {} orders (total {}) and completed {} orders",
                                        tablePaymentId,
                                        tableId,
                                        links.size,
                                        totalAmount,
                                        completeResult["completedCount"]
                                    )

                                    mapOf(
                                        "tableId" to tableId,
                                        "tablePaymentId" to tablePaymentId,
                                        "orderCount" to links.size,
                                        "totalAmount" to totalAmount,
                                        "completedCount" to completeResult["completedCount"],
                                        "message" to "Table payment created and orders completed successfully"
                                    )
                                }
                        }
                }
            }
    }
}

