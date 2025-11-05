package nl.appetit.api.logic.repository

import reactor.core.publisher.Mono

interface RestaurantRepository {
    fun existsById(id: Int): Mono<Boolean>
}

