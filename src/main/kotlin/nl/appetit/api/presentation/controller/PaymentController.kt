package nl.appetit.api.presentation.controller

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
    private val orderRepository: OrderRepository
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
        return paymentService.listPayments()
            .flatMap { payment ->
                orderRepository.findById(payment.orderId)
                    .map { order -> PaymentMapper.toResponse(payment, order) }
                    .switchIfEmpty(Mono.just(PaymentMapper.toResponse(payment, null)))
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
        return paymentService.listPaymentsForTable(tableId)
            .flatMap { payment ->
                orderRepository.findById(payment.orderId)
                    .map { order -> PaymentMapper.toResponse(payment, order) }
                    .switchIfEmpty(Mono.just(PaymentMapper.toResponse(payment, null)))
            }
    }
}

