package nl.appetit.api.presentation.controller

import jakarta.validation.Valid
import nl.appetit.api.logic.service.RestaurantService
import nl.appetit.api.presentation.dto.restaurant.CreateRestaurantRequest
import nl.appetit.api.presentation.dto.restaurant.RestaurantResponse
import nl.appetit.api.presentation.dto.restaurant.UpdateRestaurantRequest
import nl.appetit.api.presentation.mapper.RestaurantMapper
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("restaurants")
class RestaurantController(
    private val restaurantService: RestaurantService,
) {
    @GetMapping
    fun findAll(): Flux<RestaurantResponse> = restaurantService.findAll()
        .map(RestaurantMapper::toResponse)

    @PostMapping
    fun save(@RequestBody request: Mono<CreateRestaurantRequest>): Mono<RestaurantResponse> {
        return request.flatMap { restaurantRequest ->
            val test = restaurantRequest
            restaurantService.save(RestaurantMapper.toModel(restaurantRequest))
                .map(RestaurantMapper::toResponse)
        }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @Valid @RequestBody request: Mono<UpdateRestaurantRequest>): Mono<RestaurantResponse> =
        request.flatMap { restaurantRequest ->
            restaurantService.update(id, RestaurantMapper.toModel(restaurantRequest))
                .map(RestaurantMapper::toResponse)
        }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): Mono<Void> =
        restaurantService.deleteById(id)
}