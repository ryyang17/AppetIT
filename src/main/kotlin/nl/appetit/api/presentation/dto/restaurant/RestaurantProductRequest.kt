package nl.appetit.api.presentation.dto.restaurant

import com.fasterxml.jackson.annotation.JsonAlias
import java.math.BigDecimal

data class AddProductToRestaurantRequest(
    val customPrice: BigDecimal? = null,
    @JsonAlias("available")
    val isAvailable: Boolean = true
)

data class UpdateAvailabilityRequest(
    @JsonAlias("available")
    val isAvailable: Boolean
)

data class UpdatePriceRequest(
    val customPrice: BigDecimal?
)

data class ProductAvailabilityResponse(
    val isAvailable: Boolean
)

data class UpdateSuccessResponse(
    val success: Boolean = true,
    val message: String
)
