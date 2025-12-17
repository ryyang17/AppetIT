package nl.appetit.api.presentation.dto.restaurant

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class RestaurantProductRequest(
    @field:NotNull(message = "Product ID is required")
    val productId: Int,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "Custom price must be greater than 0")
    val customPrice: BigDecimal? = null,

    @field:NotNull(message = "Availability status is required")
    val isAvailable: Boolean
)

data class UpdateRestaurantProductAvailabilityRequest(
    @field:NotNull(message = "Availability status is required")
    val isAvailable: Boolean
)

data class UpdateRestaurantProductPriceRequest(
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Custom price must be greater than 0")
    val customPrice: BigDecimal?
)
