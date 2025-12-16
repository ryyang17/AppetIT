package nl.appetit.api.logic.repository

import nl.appetit.api.logic.model.Payment
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PaymentRepository {
    fun findAll(): Flux<Payment>
    fun findById(id: Int): Mono<Payment>
    fun findByOrderId(orderId: Int): Flux<Payment>
    fun findByTableId(tableId: Int): Flux<Payment>
    fun save(payment: Payment): Mono<Payment>
}

