package nl.appetit.api.logic.repository

import nl.appetit.api.logic.model.Order
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrderRepository {
    fun findAll(): Flux<Order>
    fun findByTableId(tableId: Int): Flux<Order>
    fun findByRestaurantIdAndCategoryName(restaurantId: Int, categoryName: String): Flux<Order>
    fun findByStatus(status: String): Flux<Order>
    fun findByTableIdAndStatus(tableId: Int, status: String): Flux<Order>
    fun save(order: Order): Mono<Order>
    fun deleteById(id: Int): Mono<Void>
    fun deleteAll(): Mono<Void>
    fun findById(id: Int): Mono<Order>
}