package nl.appetit.api.logic.model

data class Table(
    val id: Int? = null,
    val restaurantId: Int,
    val tableNumber: Int,
    val capacity: Int? = null,
    val isActive: Boolean = true
)

