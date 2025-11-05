package nl.appetit.api.data.repository.impl

import nl.appetit.api.logic.repository.RestaurantRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RestaurantRepositoryImpl(
    private val client: DatabaseClient
) : RestaurantRepository {
    override fun existsById(id: Int): Mono<Boolean> =
        client.sql("SELECT 1 FROM restaurant WHERE restaurant_id = :id")
            .bind("id", id)
            .map { row, _ -> row.get(0) != null }
            .one()
            .defaultIfEmpty(false)
}
