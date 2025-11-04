package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.OrderItemEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface OrderItemR2dbcRepository : ReactiveCrudRepository<OrderItemEntity, Int> {
    fun findAllByOrderId(orderId: Int): Flux<OrderItemEntity>
}
