package nl.appetit.api.logic.repository

import nl.appetit.api.logic.model.Restaurant
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RestaurantRepository {
    fun findAll(): Flux<Restaurant>
    fun findById(id: Int): Mono<Restaurant>
    fun save(restaurant: Restaurant): Mono<Restaurant>
    fun deleteById(restaurantId: Int): Mono<Void>

}