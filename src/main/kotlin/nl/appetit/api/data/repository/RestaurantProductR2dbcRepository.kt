package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.RestaurantProductEntity
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RestaurantProductR2dbcRepository : ReactiveCrudRepository<RestaurantProductEntity, Int> {

    // Spring Data JPA naming conventies
    fun findByRestaurantId(restaurantId: Int): Flux<RestaurantProductEntity>

    fun findByProductId(productId: Int): Flux<RestaurantProductEntity>

    fun findByRestaurantIdAndIsAvailable(restaurantId: Int, isAvailable: Boolean): Flux<RestaurantProductEntity>

    fun findByRestaurantIdAndProductId(restaurantId: Int, productId: Int): Mono<RestaurantProductEntity>

    @Modifying
    @Query("DELETE FROM restaurant_product WHERE restaurant_id = :restaurantId AND product_id = :productId")
    fun deleteByRestaurantIdAndProductId(restaurantId: Int, productId: Int): Mono<Void>
}
