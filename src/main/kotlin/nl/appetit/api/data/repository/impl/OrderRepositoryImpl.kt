package nl.appetit.api.data.repository.impl

import nl.appetit.api.data.entity.OrderEntity
import nl.appetit.api.data.mapper.OrderMapper
import nl.appetit.api.data.repository.OrderR2dbcRepository
import nl.appetit.api.logic.model.Order
import nl.appetit.api.logic.repository.OrderRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class OrderRepositoryImpl(
    private val db: OrderR2dbcRepository
) : OrderRepository {
    override fun findAll(): Flux<Order> =
        db.findAll()
            .map(OrderMapper::toModel)

    override fun save(order: Order): Mono<Order> =
        db.save(OrderMapper.toEntity(order))
            .map(OrderMapper::toModel)

    override fun deleteById(id: Int): Mono<Void> =
        db.deleteById(id)

    override fun deleteAll(): Mono<Void> =
        db.deleteAll()
}