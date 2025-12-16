package nl.appetit.api.logic.service

import nl.appetit.api.logic.model.Payment
import nl.appetit.api.logic.repository.OrderRepository
import nl.appetit.api.logic.repository.PaymentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Instant

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val orderRepository: OrderRepository,
    private val orderService: OrderService
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

                val paymentsFlux: Flux<Payment> = Flux.fromIterable(readyOrders)
                    .flatMap { order ->
                        val amount = order.totalAmount ?: BigDecimal.ZERO

                        val payment = Payment(
                            orderId = order.id
                                ?: throw IllegalStateException("Order without id can not be paid"),
                            paymentMethod = paymentMethod,
                            amount = amount,
                            status = "PAID",
                            idealTransactionId = null,
                            createdAt = now,
                            updatedAt = now,
                            completedAt = now
                        )

                        paymentRepository.save(payment)
                    }

                paymentsFlux
                    .collectList()
                    .flatMap { payments ->
                        val totalAmount = payments.fold(BigDecimal.ZERO) { acc, p -> acc + p.amount }

                        // After creating all payments, mark the orders as COMPLETED
                        orderService.completeOrdersForTable(tableId)
                            .map { completeResult ->
                                logger.info(
                                    "Created {} payments (total {}) and completed {} orders for table {}",
                                    payments.size,
                                    totalAmount,
                                    completeResult["completedCount"],
                                    tableId
                                )

                                mapOf(
                                    "tableId" to tableId,
                                    "paymentCount" to payments.size,
                                    "totalAmount" to totalAmount,
                                    "completedCount" to completeResult["completedCount"],
                                    "message" to "Payments created and orders completed successfully"
                                )
                            }
                    }
            }
    }

    fun listPayments(): Flux<Payment> = paymentRepository.findAll()

    fun listPaymentsForTable(tableId: Int): Flux<Payment> =
        paymentRepository.findByTableId(tableId)
}

