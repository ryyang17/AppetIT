package nl.appetit.api.logic.service

import nl.appetit.api.data.entity.RestaurantProductEntity
import nl.appetit.api.data.repository.RestaurantProductR2dbcRepository
import nl.appetit.api.logic.repository.ProductRepository
import nl.appetit.api.logic.repository.RestaurantRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Service
class RestaurantProductService(
    private val restaurantProductRepository: RestaurantProductR2dbcRepository,
    private val productRepository: ProductRepository,
    private val restaurantRepository: RestaurantRepository
) {
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

    fun updateProductToRestaurant(
        restaurantId: Int,
        productId: Int,
        customPrice: BigDecimal?,
        isAvailable: Boolean
    ): Mono<RestaurantProductEntity> {
        return restaurantProductRepository.findByRestaurantIdAndProductId(restaurantId, productId)
            .switchIfEmpty(Mono.error(RuntimeException("Restaurant product combination not found: restaurantId=$restaurantId, productId=$productId")))
            .flatMap { existingEntity ->
                val updatedEntity = existingEntity.copy(
                    customPrice = customPrice,
                    isAvailable = isAvailable
                )
                restaurantProductRepository.save(updatedEntity)
            }
    }

    fun updateProductAvailability(
        restaurantId: Int,
        productId: Int,
        isAvailable: Boolean
    ): Mono<Void> {
        return restaurantProductRepository.findByRestaurantIdAndProductId(restaurantId, productId)
            .switchIfEmpty(Mono.error(RuntimeException("Restaurant product combination not found: restaurantId=$restaurantId, productId=$productId")))
            .flatMap { existingEntity ->
                val updatedEntity = existingEntity.copy(isAvailable = isAvailable)
                restaurantProductRepository.save(updatedEntity)
            }
            .then()
    }

    fun updateProductPrice(
        restaurantId: Int,
        productId: Int,
        customPrice: BigDecimal?
    ): Mono<Void> {
        return restaurantProductRepository.findByRestaurantIdAndProductId(restaurantId, productId)
            .switchIfEmpty(Mono.error(RuntimeException("Restaurant product combination not found: restaurantId=$restaurantId, productId=$productId")))
            .flatMap { existingEntity ->
                val updatedEntity = existingEntity.copy(customPrice = customPrice)
                restaurantProductRepository.save(updatedEntity)
            }
            .then()
    }

    fun getProductsByRestaurant(restaurantId: Int): Flux<RestaurantProductEntity> {
        return restaurantProductRepository.findByRestaurantId(restaurantId)
    }

    fun getProductByRestaurant(restaurantId: Int, productId: Int): Mono<RestaurantProductEntity> {
        return restaurantProductRepository.findByRestaurantIdAndProductId(restaurantId, productId)
            .switchIfEmpty(Mono.error(RuntimeException("Restaurant product combination not found: restaurantId=$restaurantId, productId=$productId")))
    }

    fun removeProductFromRestaurant(restaurantId: Int, productId: Int): Mono<Void> {
        return restaurantProductRepository.deleteByRestaurantIdAndProductId(restaurantId, productId)
    }

    fun linkAllProductsToNewRestaurant(restaurantId: Int): Mono<Void> {
        return productRepository.findAll()
            .flatMap { product ->
                val entity = RestaurantProductEntity(
                    restaurantId = restaurantId,
                    productId = product.id!!,
                    customPrice = null,
                    isAvailable = false
                )
                restaurantProductRepository.save(entity)
            }
            .then()
    }


}
