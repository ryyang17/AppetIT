package nl.appetit.api.logic.repository

import nl.appetit.api.data.entity.OrderEntity
import nl.appetit.api.logic.model.Order
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrderRepository {
    fun findAll(): Flux<Order>
    fun findByTableId(tableId: Int): Flux<Order>
    fun save(order: Order): Mono<Order>
    fun deleteById(id: Int): Mono<Void>
    fun deleteAll(): Mono<Void>
}