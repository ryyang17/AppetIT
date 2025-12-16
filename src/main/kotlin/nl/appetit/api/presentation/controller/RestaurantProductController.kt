package nl.appetit.api.presentation.controller

import nl.appetit.api.logic.service.RestaurantProductService
import nl.appetit.api.data.entity.RestaurantProductEntity
import nl.appetit.api.presentation.dto.restaurant.AddProductToRestaurantRequest
import nl.appetit.api.presentation.dto.restaurant.UpdateAvailabilityRequest
import nl.appetit.api.presentation.dto.restaurant.UpdatePriceRequest
import nl.appetit.api.presentation.dto.restaurant.ProductAvailabilityResponse
import nl.appetit.api.presentation.dto.restaurant.RestaurantProductResponse
import nl.appetit.api.presentation.dto.restaurant.UpdateSuccessResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/restaurants")
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
    fun updateProductAvailability(
        @PathVariable restaurantId: Int,
        @PathVariable productId: Int,
        @RequestBody request: UpdateAvailabilityRequest
    ): Mono<UpdateSuccessResponse> {
        return restaurantProductService.updateProductAvailability(
            restaurantId, productId, request.isAvailable
        ).then(Mono.just(UpdateSuccessResponse(success = true, message = "Product availability updated successfully")))
    }

    @PatchMapping("/{restaurantId}/products/{productId}/price")
    fun updateProductPrice(
        @PathVariable restaurantId: Int,
        @PathVariable productId: Int,
        @RequestBody request: UpdatePriceRequest
    ): Mono<UpdateSuccessResponse> {
        return restaurantProductService.updateProductPrice(
            restaurantId, productId, request.customPrice
        ).then(Mono.just(UpdateSuccessResponse(success = true, message = "Product price updated successfully")))
    }

    @GetMapping("/{restaurantId}/products/{productId}/availability")
    fun getProductAvailability(
        @PathVariable restaurantId: Int,
        @PathVariable productId: Int
    ): Mono<ProductAvailabilityResponse> {
        return restaurantProductService.isProductAvailableInRestaurant(restaurantId, productId)
            .map { ProductAvailabilityResponse(it) }
    }

    @DeleteMapping("/{restaurantId}/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeProductFromRestaurant(
        @PathVariable restaurantId: Int,
        @PathVariable productId: Int
    ): Mono<Void> {
        return restaurantProductService.removeProductFromRestaurant(restaurantId, productId)
    }
}
