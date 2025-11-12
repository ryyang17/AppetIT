package nl.appetit.api.presentation.dto.restaurant

data class RestaurantResponse(
    val id: Int?,
    val name: String,
    val address: String,
    val phone: String,
    val email: String,
    val isActive: Boolean,
)
