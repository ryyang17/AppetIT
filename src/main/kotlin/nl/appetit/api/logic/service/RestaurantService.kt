package nl.appetit.api.logic.service

import nl.appetit.api.logic.exception.NotFoundException
import nl.appetit.api.logic.model.Restaurant
import nl.appetit.api.logic.model.RestaurantUpdate
import nl.appetit.api.logic.repository.RestaurantRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RestaurantService(
    private val restaurantRepository: RestaurantRepository,
) {

    fun findAll(): Flux<Restaurant> = restaurantRepository.findAll()

    fun save(restaurant: Restaurant): Mono<Restaurant> = restaurantRepository.save(restaurant)

    fun deleteById(restaurantId: Int): Mono<Void> = restaurantRepository.deleteById(restaurantId)

    fun update(id: Int, restaurant: RestaurantUpdate): Mono<Restaurant> = restaurantRepository.findById(id)
        .switchIfEmpty(Mono.error(NotFoundException("Restaurant with id $id not found")))
        .flatMap { existing ->
            restaurantRepository.save(existing.copy(
                name = restaurant.name ?: existing.name,
                address = restaurant.address ?: existing.address,
                phone = restaurant.phone ?: existing.phone,
                email = restaurant.email ?: existing.email,
                isActive = restaurant.isActive ?: existing.isActive
            ))
        }

}