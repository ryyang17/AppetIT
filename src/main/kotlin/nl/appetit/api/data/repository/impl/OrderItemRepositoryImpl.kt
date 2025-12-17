package nl.appetit.api.data.repository.impl

import nl.appetit.api.data.entity.OrderItemEntity
import nl.appetit.api.data.mapper.OrderItemMapper
import nl.appetit.api.data.repository.OrderItemR2dbcRepository
import nl.appetit.api.logic.model.OrderItem
import nl.appetit.api.logic.repository.OrderItemRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class OrderItemRepositoryImpl(
    private val db: OrderItemR2dbcRepository
): OrderItemRepository {
    override fun findAll(): Flux<OrderItem> =
        db.findAll()
            .map(OrderItemMapper::toModel)

    override fun findAllByOrderId(orderId: Int): Flux<OrderItem> =
        db.findAllByOrderId(orderId)
            .map(OrderItemMapper::toModel)

    override fun findById(id: Int): Mono<OrderItem> =
        db.findById(id).map(OrderItemMapper::toModel)

    override fun save(item: OrderItem): Mono<OrderItem> =
        db.save(OrderItemMapper.toEntity(item))
            .map(OrderItemMapper::toModel)

    override fun deleteById(id: Int): Mono<Void> =
        db.deleteById(id)

    override fun deleteAll(): Mono<Void> =
        db.deleteAll()

    override fun assignStaff(itemId: Int, staffId: Int?): Mono<OrderItem> =
        db.findById(itemId)
            .flatMap { entity ->
                val updated = entity.copy(staffId = staffId)
                db.save(updated)
            }
            .map(OrderItemMapper::toModel)
}