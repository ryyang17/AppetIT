package nl.appetit.api.presentation.dto.restaurant

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class RestaurantProductResponse(
    val productId: Int,
    val productName: String,
    val basePrice: BigDecimal,
    val customPrice: BigDecimal? = null,
    // Use @JsonProperty to explicitly control JSON serialization
    // This ensures "isAvailable" is serialized as "isAvailable" (not "available")
    // This matches the frontend interface expectation
    @JsonProperty("isAvailable")
    val isAvailable: Boolean,
    val restaurantId: Int,
    val restaurantName: String
)

