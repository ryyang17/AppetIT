package nl.appetit.api.presentation.dto.restaurant

import java.math.BigDecimal

data class AddProductToRestaurantRequest(
    val customPrice: BigDecimal? = null,
    val isAvailable: Boolean = true
)

data class UpdateAvailabilityRequest(
    val isAvailable: Boolean
)

data class UpdatePriceRequest(
    val customPrice: BigDecimal?
)

data class ProductAvailabilityResponse(
    val isAvailable: Boolean
)


