package nl.appetit.api.presentation.dto.restaurant

import java.math.BigDecimal

data class RestaurantProductResponse(
    val productId: Int,
    val productName: String,
    val basePrice: BigDecimal,
    val customPrice: BigDecimal? = null,
    val isAvailable: Boolean,
    val restaurantId: Int,
    val restaurantName: String
)

