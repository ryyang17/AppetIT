package nl.appetit.api.presentation.mapper

import nl.appetit.api.data.entity.RestaurantProductEntity
import nl.appetit.api.logic.model.Product
import nl.appetit.api.logic.model.Restaurant
import nl.appetit.api.presentation.dto.restaurant.RestaurantProductResponse
import java.math.BigDecimal

object RestaurantProductMapper {

    fun toResponse(
        entity: RestaurantProductEntity,
        product: Product,
        restaurant: Restaurant
    ): RestaurantProductResponse {
        val finalPrice = entity.customPrice ?: product.price

        return RestaurantProductResponse(
            productId = product.id!!,
            productName = product.name,
            basePrice = product.price,
            customPrice = entity.customPrice,
            // Restaurant-specific availability - restaurants can enable/disable products independently
            isAvailable = entity.isAvailable,
            restaurantId = restaurant.id!!,
            restaurantName = restaurant.name,
            categoryId = product.categoryId,
            description = product.description,
            imageUrl = product.imageUrl
        )
    }

    fun toResponse(
        entity: RestaurantProductEntity,
        productName: String,
        basePrice: BigDecimal,
        restaurantName: String
    ): RestaurantProductResponse {
        val finalPrice = entity.customPrice ?: basePrice

        return RestaurantProductResponse(
            productId = entity.productId,
            productName = productName,
            basePrice = basePrice,
            customPrice = entity.customPrice,
            isAvailable = entity.isAvailable,
            restaurantId = entity.restaurantId,
            restaurantName = restaurantName
        )
    }
}
