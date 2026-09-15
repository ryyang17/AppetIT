package nl.appetit.api.presentation.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import nl.appetit.api.logic.repository.ProductRepository
import nl.appetit.api.logic.repository.RestaurantRepository
import nl.appetit.api.logic.service.RestaurantProductService
import nl.appetit.api.presentation.dto.restaurant.RestaurantProductRequest
import nl.appetit.api.presentation.dto.restaurant.RestaurantProductResponse
import nl.appetit.api.presentation.dto.restaurant.UpdateRestaurantProductAvailabilityRequest
import nl.appetit.api.presentation.dto.restaurant.UpdateRestaurantProductPriceRequest
import nl.appetit.api.presentation.mapper.RestaurantProductMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/restaurants/{restaurantId}/products")
@Tag(name = "Restaurant Products", description = "Endpoints for managing products per restaurant (availability, custom pricing)")
class RestaurantProductController(
    private val restaurantProductService: RestaurantProductService,
    private val productRepository: ProductRepository,
    private val restaurantRepository: RestaurantRepository
) {
    private val logger = LoggerFactory.getLogger(RestaurantProductController::class.java)

    @GetMapping
    @Operation(
        summary = "Get all products for a restaurant",
        description = "Retrieves all products associated with a specific restaurant, including their custom prices and availability status"
    )
    fun getProductsByRestaurant(
        @Parameter(description = "The ID of the restaurant", required = true)
        @PathVariable restaurantId: Int,
        @Parameter(description = "Filter to only show available products", required = false)
        @RequestParam(required = false, defaultValue = "false") availableOnly: Boolean
    ): Flux<RestaurantProductResponse> {
        val productsFlux = if (availableOnly) {
            restaurantProductService.getProductsByRestaurant(restaurantId)
                .filter { it.isAvailable }
        } else {
            restaurantProductService.getProductsByRestaurant(restaurantId)
        }
        
        return productsFlux
            .flatMap { restaurantProduct ->
                val productMono = productRepository.findById(restaurantProduct.productId)
                val restaurantMono = restaurantRepository.findById(restaurantProduct.restaurantId)

                Mono.zip(productMono, restaurantMono) { product, restaurant ->
                    RestaurantProductMapper.toResponse(restaurantProduct, product, restaurant)
                }
            }
    }


    @GetMapping("/{productId}")
    @Operation(
        summary = "Get a specific product for a restaurant",
        description = "Retrieves a specific product for a restaurant with its custom price and availability status"
    )
    fun getProductByRestaurant(
        @Parameter(description = "The ID of the restaurant", required = true)
        @PathVariable restaurantId: Int,
        @Parameter(description = "The ID of the product", required = true)
        @PathVariable productId: Int
    ): Mono<RestaurantProductResponse> {
        return restaurantProductService.getProductByRestaurant(restaurantId, productId)
            .flatMap { restaurantProduct ->
                val productMono = productRepository.findById(restaurantProduct.productId)
                val restaurantMono = restaurantRepository.findById(restaurantProduct.restaurantId)

                Mono.zip(productMono, restaurantMono) { product, restaurant ->
                    RestaurantProductMapper.toResponse(restaurantProduct, product, restaurant)
                }
            }
            .onErrorMap { e ->
                if (e.message?.contains("not found") == true) {
                    ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
                } else {
                    e
                }
            }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Add a product to a restaurant",
        description = "Links a product to a restaurant with optional custom pricing and availability status"
    )
    fun addProductToRestaurant(
        @Parameter(description = "The ID of the restaurant", required = true)
        @PathVariable restaurantId: Int,
        @Parameter(description = "The product data to add", required = true)
        @Valid @RequestBody request: RestaurantProductRequest
    ): Mono<RestaurantProductResponse> {
        return restaurantRepository.findById(restaurantId)
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found")))
            .flatMap { restaurant ->
                productRepository.findById(request.productId)
                    .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")))
                    .flatMap { product ->
                        restaurantProductService.addProductToRestaurant(
                            restaurantId = restaurantId,
                            productId = request.productId,
                            customPrice = request.customPrice,
                            isAvailable = request.isAvailable
                        ).map { entity ->
                            RestaurantProductMapper.toResponse(entity, product, restaurant)
                        }
                    }
            }
    }

    @PutMapping("/{productId}")
    @Operation(
        summary = "Update a product in a restaurant",
        description = "Updates both the custom price and availability status of a product in a restaurant"
    )
    fun updateProductInRestaurant(
        @Parameter(description = "The ID of the restaurant", required = true)
        @PathVariable restaurantId: Int,
        @Parameter(description = "The ID of the product", required = true)
        @PathVariable productId: Int,
        @Parameter(description = "The product update data", required = true)
        @Valid @RequestBody request: RestaurantProductRequest
    ): Mono<RestaurantProductResponse> {
        return restaurantProductService.updateProductToRestaurant(
            restaurantId = restaurantId,
            productId = productId,
            customPrice = request.customPrice,
            isAvailable = request.isAvailable
        ).flatMap { entity ->
            val productMono = productRepository.findById(entity.productId)
            val restaurantMono = restaurantRepository.findById(entity.restaurantId)

            Mono.zip(productMono, restaurantMono) { product, restaurant ->
                RestaurantProductMapper.toResponse(entity, product, restaurant)
            }
        }.onErrorMap { e ->
            if (e.message?.contains("not found") == true) {
                ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
            } else {
                e
            }
        }
    }

    @PatchMapping("/{productId}/availability")
    @Operation(
        summary = "Update product availability in a restaurant",
        description = "Updates the availability status of a specific product in a restaurant"
    )
    fun updateProductAvailability(
        @Parameter(description = "The ID of the restaurant", required = true)
        @PathVariable restaurantId: Int,
        @Parameter(description = "The ID of the product", required = true)
        @PathVariable productId: Int,
        @Parameter(description = "The availability update data", required = true)
        @Valid @RequestBody request: UpdateRestaurantProductAvailabilityRequest
    ): Mono<Void> {
        return restaurantProductService.updateProductAvailability(
            restaurantId = restaurantId,
            productId = productId,
            isAvailable = request.isAvailable
        )
        .onErrorMap { e ->
            if (e.message?.contains("not found") == true) {
                ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
            } else {
                e
            }
        }
    }

    @PatchMapping("/{productId}/price")
    @Operation(
        summary = "Update product custom price in a restaurant",
        description = "Updates the custom price of a specific product in a restaurant. Set to null to use the base product price."
    )
    fun updateProductPrice(
        @Parameter(description = "The ID of the restaurant", required = true)
        @PathVariable restaurantId: Int,
        @Parameter(description = "The ID of the product", required = true)
        @PathVariable productId: Int,
        @Parameter(description = "The price update data", required = true)
        @Valid @RequestBody request: UpdateRestaurantProductPriceRequest
    ): Mono<Void> {
        return restaurantProductService.updateProductPrice(
            restaurantId = restaurantId,
            productId = productId,
            customPrice = request.customPrice
        ).onErrorMap { e ->
            if (e.message?.contains("not found") == true) {
                ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
            } else {
                e
            }
        }
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Remove a product from a restaurant",
        description = "Removes the association between a product and a restaurant"
    )
    fun removeProductFromRestaurant(
        @Parameter(description = "The ID of the restaurant", required = true)
        @PathVariable restaurantId: Int,
        @Parameter(description = "The ID of the product", required = true)
        @PathVariable productId: Int
    ): Mono<Void> {
        return restaurantProductService.removeProductFromRestaurant(restaurantId, productId)
    }
}
