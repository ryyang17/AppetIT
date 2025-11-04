package nl.appetit.api.logic.repository

import nl.appetit.api.data.entity.OrderItemEntity
import nl.appetit.api.logic.model.OrderItem
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrderItemRepository {
    fun findAll(): Flux<OrderItem>
    fun findAllByOrderId(orderId: Int): Flux<OrderItem>
    fun save(item: OrderItem): Mono<OrderItem>
    fun deleteById(id: Int): Mono<Void>
    fun deleteAll(): Mono<Void>
}