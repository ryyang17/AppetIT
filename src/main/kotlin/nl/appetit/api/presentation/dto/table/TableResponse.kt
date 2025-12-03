package nl.appetit.api.presentation.dto.table

data class TableResponse(
    val id: Int,
    val restaurantId: Int,
    val tableNumber: Int,
    val capacity: Int? = null,
    val isActive: Boolean
)
