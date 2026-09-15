package nl.appetit.api.data.repository.impl

import nl.appetit.api.data.mapper.RestaurantMapper
import nl.appetit.api.data.repository.RestaurantR2dbcRepository
import nl.appetit.api.logic.model.Restaurant
import nl.appetit.api.logic.repository.RestaurantRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class RestaurantRepositoryImpl(
    private val restaurantR2dbcRepository: RestaurantR2dbcRepository
) : RestaurantRepository {
    override fun findAll(): Flux<Restaurant> =
        restaurantR2dbcRepository.findAll()
            .map(RestaurantMapper::toModel)

    override fun save(restaurant: Restaurant): Mono<Restaurant> =
        restaurantR2dbcRepository.save(RestaurantMapper.toEntity(restaurant))
            .map(RestaurantMapper::toModel)

    override fun deleteById(restaurantId: Int): Mono<Void> =
        restaurantR2dbcRepository.deleteById(restaurantId)

    override fun findById(id: Int): Mono<Restaurant> =
        restaurantR2dbcRepository.findById(id)
            .map(RestaurantMapper::toModel)
}