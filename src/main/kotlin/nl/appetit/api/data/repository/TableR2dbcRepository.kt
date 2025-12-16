package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.TableEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface TableR2dbcRepository : ReactiveCrudRepository<TableEntity, Int> {
    @Query("SELECT * FROM \"table\" WHERE restaurant_id = :restaurantId")
    fun findAllByRestaurantId(restaurantId: Int): Flux<TableEntity>
}
