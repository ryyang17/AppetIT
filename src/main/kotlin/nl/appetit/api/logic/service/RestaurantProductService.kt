package nl.appetit.api.logic.service

import nl.appetit.api.data.entity.RestaurantProductEntity
import nl.appetit.api.data.repository.RestaurantProductR2dbcRepository
import nl.appetit.api.logic.repository.ProductRepository
import nl.appetit.api.logic.repository.RestaurantRepository
import nl.appetit.api.presentation.dto.restaurant.RestaurantProductResponse
import nl.appetit.api.presentation.mapper.RestaurantProductMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Service
class RestaurantProductService(
    private val restaurantProductRepository: RestaurantProductR2dbcRepository,
    private val productRepository: ProductRepository,
    private val restaurantRepository: RestaurantRepository
) {
     // Koppel een product aan een restaurant
    fun addProductToRestaurant(
        restaurantId: Int,
        productId: Int,
        customPrice: BigDecimal? = null,
        isAvailable: Boolean
    ): Mono<RestaurantProductEntity> {
        val entity = RestaurantProductEntity(
            restaurantId = restaurantId,
            productId = productId,
            customPrice = customPrice,
            isAvailable = isAvailable
        )
        return restaurantProductRepository.save(entity)
    }

    // Haal alle producten op voor een specifiek restaurant
    fun getProductsByRestaurant(restaurantId: Int): Flux<RestaurantProductEntity> {
        return restaurantProductRepository.findByRestaurantId(restaurantId)
    }

    // Haal alle beschikbare producten op voor een specifiek restaurant
    fun getAvailableProductsByRestaurant(restaurantId: Int): Flux<RestaurantProductEntity> {
        return restaurantProductRepository.findByRestaurantIdAndIsAvailable(restaurantId, true)
    }

    // Update de beschikbaarheid van een product in een restaurant
    @Transactional
    fun updateProductAvailability(
        restaurantId: Int,
        productId: Int,
        isAvailable: Boolean
    ): Mono<Void> {
        return restaurantProductRepository.findByRestaurantIdAndProductId(restaurantId, productId)
            .switchIfEmpty(Mono.error(RuntimeException("Restaurant product combination not found: restaurantId=$restaurantId, productId=$productId")))
            .flatMap {
                restaurantProductRepository.updateAvailability(restaurantId, productId, isAvailable)
            }
    }

    // Update de aangepaste prijs voor een product in een restaurant
    @Transactional
    fun updateProductPrice(
        restaurantId: Int,
        productId: Int,
        customPrice: BigDecimal?
    ): Mono<Void> {
        return restaurantProductRepository.findByRestaurantIdAndProductId(restaurantId, productId)
            .switchIfEmpty(Mono.error(RuntimeException("Restaurant product combination not found: restaurantId=$restaurantId, productId=$productId")))
            .flatMap {
                restaurantProductRepository.updateCustomPrice(restaurantId, productId, customPrice)
            }
    }

    // Verwijder de koppeling tussen een product en restauranty
    fun removeProductFromRestaurant(restaurantId: Int, productId: Int): Mono<Void> {
        return restaurantProductRepository.deleteByRestaurantIdAndProductId(restaurantId, productId)
    }

    // Controleer of een product beschikbaar is in een restaurant
    fun isProductAvailableInRestaurant(restaurantId: Int, productId: Int): Mono<Boolean> {
        return restaurantProductRepository.findByRestaurantIdAndProductId(restaurantId, productId)
            .flatMap { restaurantProduct ->
                productRepository.findById(productId)
                    .map { product -> product.isAvailable && restaurantProduct.isAvailable }
                    .defaultIfEmpty(false) // Handle case where product doesn't exist
            }
            .defaultIfEmpty(false) // Handle case where restaurant-product relation doesn't exist
    }

    // Koppel alle bestaande producten aan een nieuw restaurant
    fun linkAllProductsToNewRestaurant(restaurantId: Int): Mono<Void> {
        return productRepository.findAll()
            .flatMap { product ->
                val entity = RestaurantProductEntity(
                    restaurantId = restaurantId,
                    productId = product.id!!,
                    customPrice = null, // Gebruik standaard product prijs
                    isAvailable = product.isAvailable // Neem over van product
                )
                restaurantProductRepository.save(entity)
            }
            .then()
    }

    /**
     * Haal alle producten op voor een restaurant met rijke informatie
     */
    fun getProductsByRestaurantWithDetails(restaurantId: Int): Flux<RestaurantProductResponse> {
        return restaurantProductRepository.findByRestaurantId(restaurantId)
            .flatMap { restaurantProduct ->
                val productMono = productRepository.findById(restaurantProduct.productId)
                val restaurantMono = restaurantRepository.findById(restaurantProduct.restaurantId)

                Mono.zip(productMono, restaurantMono) { product, restaurant ->
                    RestaurantProductMapper.toResponse(restaurantProduct, product, restaurant)
                }
            }
    }

    /**
     * Haal alleen beschikbare producten op voor een restaurant met rijke informatie
     */
    fun getAvailableProductsByRestaurantWithDetails(restaurantId: Int): Flux<RestaurantProductResponse> {
        return restaurantProductRepository.findByRestaurantIdAndIsAvailable(restaurantId, true)
            .flatMap { restaurantProduct ->
                val productMono = productRepository.findById(restaurantProduct.productId)
                val restaurantMono = restaurantRepository.findById(restaurantProduct.restaurantId)

                Mono.zip(productMono, restaurantMono) { product, restaurant ->
                    RestaurantProductMapper.toResponse(restaurantProduct, product, restaurant)
                }
            }
            .filter { response -> response.isAvailable } // Filter out products that are globally unavailable
    }
}
