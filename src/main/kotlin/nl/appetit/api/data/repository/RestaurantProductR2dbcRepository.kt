package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.RestaurantProductEntity
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal

interface RestaurantProductR2dbcRepository : ReactiveCrudRepository<RestaurantProductEntity, Int> {

    // Spring Data JPA naming conventies
    fun findByRestaurantId(restaurantId: Int): Flux<RestaurantProductEntity>

    fun findByProductId(productId: Int): Flux<RestaurantProductEntity>

    fun findByRestaurantIdAndIsAvailable(restaurantId: Int, isAvailable: Boolean): Flux<RestaurantProductEntity>

    fun findByRestaurantIdAndProductId(restaurantId: Int, productId: Int): Mono<RestaurantProductEntity>

    // Custom queries alleen waar nodig voor complexere operaties
    @Modifying
    @Query("UPDATE restaurant_product SET is_available = :isAvailable, updated_at = CURRENT_TIMESTAMP WHERE restaurant_id = :restaurantId AND product_id = :productId")
    fun updateAvailability(restaurantId: Int, productId: Int, isAvailable: Boolean): Mono<Void>

    @Modifying
    @Query("UPDATE restaurant_product SET custom_price = :customPrice, updated_at = CURRENT_TIMESTAMP WHERE restaurant_id = :restaurantId AND product_id = :productId")
    fun updateCustomPrice(restaurantId: Int, productId: Int, customPrice: BigDecimal?): Mono<Void>
}
