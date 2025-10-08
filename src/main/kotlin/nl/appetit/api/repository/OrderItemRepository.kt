package nl.appetit.api.repository

import nl.appetit.api.model.OrderItem
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface OrderItemRepository : ReactiveCrudRepository<OrderItem, Int> {
    fun findAllByOrderId(orderId: Int): Flux<OrderItem>
}
