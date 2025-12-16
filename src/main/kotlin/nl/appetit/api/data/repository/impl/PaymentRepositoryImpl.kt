package nl.appetit.api.data.repository.impl

import nl.appetit.api.data.mapper.PaymentMapper
import nl.appetit.api.data.repository.PaymentR2dbcRepository
import nl.appetit.api.logic.model.Payment
import nl.appetit.api.logic.repository.PaymentRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class PaymentRepositoryImpl(
    private val db: PaymentR2dbcRepository
) : PaymentRepository {

    override fun findAll(): Flux<Payment> =
        db.findAll().map(PaymentMapper::toModel)

    override fun findById(id: Int): Mono<Payment> =
        db.findById(id).map(PaymentMapper::toModel)

    override fun findByOrderId(orderId: Int): Flux<Payment> =
        db.findByOrderId(orderId).map(PaymentMapper::toModel)

    override fun findByTableId(tableId: Int): Flux<Payment> =
        db.findByTableId(tableId).map(PaymentMapper::toModel)

    override fun save(payment: Payment): Mono<Payment> =
        db.save(PaymentMapper.toEntity(payment)).map(PaymentMapper::toModel)
}

