package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.RestaurantEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface RestaurantR2dbcRepository: ReactiveCrudRepository<RestaurantEntity, Int> {

}