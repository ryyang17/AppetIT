package nl.appetit.api.logic.model

data class RestaurantUpdate(
    val name: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val isActive: Boolean? = null
)
