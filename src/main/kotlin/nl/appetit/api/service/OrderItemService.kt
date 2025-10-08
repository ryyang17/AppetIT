package nl.appetit.api.service

import nl.appetit.api.model.OrderItem
import nl.appetit.api.repository.OrderItemRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderItemService(
    private val db: OrderItemRepository
) {
    fun getAllOrderItems(): Flux<OrderItem> = db.findAll()

    fun getItemsByOrder(orderId: Int): Flux<OrderItem> = db.findAllByOrderId(orderId)

    fun insertOrderItem(item: OrderItem): Mono<OrderItem> = db.save(item)

    fun deleteOrderItemById(id: Int): Mono<Void> = db.deleteById(id)

    fun deleteAllOrderItems(): Mono<Void> = db.deleteAll()
}
