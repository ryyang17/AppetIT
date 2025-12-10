package nl.appetit.api.presentation.controller

import nl.appetit.api.logic.service.RestaurantProductService
import nl.appetit.api.data.entity.RestaurantProductEntity
import nl.appetit.api.presentation.dto.restaurant.AddProductToRestaurantRequest
import nl.appetit.api.presentation.dto.restaurant.UpdateAvailabilityRequest
import nl.appetit.api.presentation.dto.restaurant.UpdatePriceRequest
import nl.appetit.api.presentation.dto.restaurant.ProductAvailabilityResponse
import nl.appetit.api.presentation.dto.restaurant.RestaurantProductResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/restaurants")
class RestaurantProductController(
    private val restaurantProductService: RestaurantProductService
) {

    @PostMapping("/{restaurantId}/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun addProductToRestaurant(
        @PathVariable restaurantId: Int,
        @PathVariable productId: Int,
        @RequestBody request: AddProductToRestaurantRequest? = null
    ): Mono<RestaurantProductEntity> {
        return restaurantProductService.addProductToRestaurant(
            restaurantId = restaurantId,
            productId = productId,
            customPrice = request?.customPrice,
            isAvailable = request?.isAvailable ?: true
        )
    }

    @GetMapping("/{restaurantId}/products")
    fun getProductsByRestaurant(
        @PathVariable restaurantId: Int,
        @RequestParam(defaultValue = "false") availableOnly: Boolean
    ): Flux<RestaurantProductResponse> {
        return if (availableOnly) {
            restaurantProductService.getAvailableProductsByRestaurantWithDetails(restaurantId)
        } else {
            restaurantProductService.getProductsByRestaurantWithDetails(restaurantId)
        }
    }

    @PatchMapping("/{restaurantId}/products/{productId}/availability")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateProductAvailability(
        @PathVariable restaurantId: Int,
        @PathVariable productId: Int,
        @RequestBody request: UpdateAvailabilityRequest
    ): Mono<Void> {
        return restaurantProductService.updateProductAvailability(
            restaurantId, productId, request.isAvailable
        )
    }

    @PatchMapping("/{restaurantId}/products/{productId}/price")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateProductPrice(
        @PathVariable restaurantId: Int,
        @PathVariable productId: Int,
        @RequestBody request: UpdatePriceRequest
    ): Mono<Void> {
        return restaurantProductService.updateProductPrice(
            restaurantId, productId, request.customPrice
        )
    }

}
