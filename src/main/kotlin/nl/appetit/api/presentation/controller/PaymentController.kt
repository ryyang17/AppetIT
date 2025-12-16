package nl.appetit.api.presentation.controller

import nl.appetit.api.data.repository.TablePaymentOrderR2dbcRepository
import nl.appetit.api.data.repository.TablePaymentR2dbcRepository
import nl.appetit.api.logic.repository.OrderRepository
import nl.appetit.api.logic.service.PaymentService
import nl.appetit.api.presentation.dto.payment.PaymentRequest
import nl.appetit.api.presentation.dto.payment.PaymentResponse
import nl.appetit.api.presentation.mapper.PaymentMapper
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/payments")
class PaymentController(
    private val paymentService: PaymentService,
    private val orderRepository: OrderRepository,
    private val tablePaymentRepository: TablePaymentR2dbcRepository,
    private val tablePaymentOrderRepository: TablePaymentOrderR2dbcRepository
) {

    private val logger = LoggerFactory.getLogger(PaymentController::class.java)

    /**
     * Create payments for all READY orders of a table and mark those orders as COMPLETED.
     */
    @PostMapping("/table/{tableId}")
    fun createPaymentsForTable(
        @PathVariable tableId: Int,
        @RequestBody(required = false) request: Mono<PaymentRequest>?
    ): Mono<Map<String, Any?>> {
        logger.info("POST /payments/table/{} called", tableId)

        val methodMono = request ?: Mono.just(PaymentRequest())

        return methodMono
            .flatMap { body ->
                paymentService.createPaymentsForTable(tableId, body.paymentMethod)
            }
    }

    /**
     * List all payments, including the tableId (via the related order).
     */
    @GetMapping
    fun listPayments(): Flux<PaymentResponse> {
        logger.info("GET /payments called")
        return tablePaymentRepository.findAllOrderByCreatedAtDesc()
            .flatMap { tablePayment ->
                tablePaymentOrderRepository.findByTablePaymentId(tablePayment.id!!)
                    .flatMap { link ->
                        orderRepository.findById(link.orderId)
                    }
                    .flatMap { order ->
                        // Reuse existing mapper chain to convert Order -> OrderResponse
                        // by leveraging OrderService enrichment behaviour would be ideal,
                        // but to keep dependencies simple we call through OrderController mappers.
                        // For now, we map only basic order fields via a minimal OrderResponse constructor.
                        // However, to keep this change focused, we'll retrieve enriched responses
                        // via OrderService if needed in a follow-up.
                        Mono.just(
                            nl.appetit.api.presentation.mapper.OrderMapper.toResponse(order, null)
                        )
                    }
                    .collectList()
                    .map { orderResponses ->
                        PaymentMapper.toResponse(tablePayment, orderResponses)
                    }
            }
    }

    /**
     * Get a single payment (table payment) by id, including its related orders.
     */
    @GetMapping("/{id}")
    fun getPaymentById(
        @PathVariable id: Int
    ): Mono<PaymentResponse> {
        logger.info("GET /payments/{} called", id)
        return tablePaymentRepository.findById(id)
            .switchIfEmpty(Mono.error(nl.appetit.api.logic.exception.NotFoundException("Table payment $id not found")))
            .flatMap { tablePayment ->
                tablePaymentOrderRepository.findByTablePaymentId(tablePayment.id!!)
                    .flatMap { link ->
                        orderRepository.findById(link.orderId)
                    }
                    .flatMap { order ->
                        Mono.just(
                            nl.appetit.api.presentation.mapper.OrderMapper.toResponse(order, null)
                        )
                    }
                    .collectList()
                    .map { orderResponses ->
                        PaymentMapper.toResponse(tablePayment, orderResponses)
                    }
            }
    }

    /**
     * List all payments for a specific table.
     */
    @GetMapping("/table/{tableId}")
    fun listPaymentsForTable(
        @PathVariable tableId: Int
    ): Flux<PaymentResponse> {
        logger.info("GET /payments/table/{} called", tableId)
        return tablePaymentRepository.findByTableIdOrderByCreatedAtDesc(tableId)
            .flatMap { tablePayment ->
                tablePaymentOrderRepository.findByTablePaymentId(tablePayment.id!!)
                    .flatMap { link ->
                        orderRepository.findById(link.orderId)
                    }
                    .flatMap { order ->
                        Mono.just(
                            nl.appetit.api.presentation.mapper.OrderMapper.toResponse(order, null)
                        )
                    }
                    .collectList()
                    .map { orderResponses ->
                        PaymentMapper.toResponse(tablePayment, orderResponses)
                    }
            }
    }
}

