package nl.appetit.api.presentation.dto.table

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class TableRequest(
    @field:NotNull(message = "Restaurant ID is required")
    @JsonProperty("restaurantId")
    val restaurantId: Int,

    @field:NotNull(message = "Table number is required")
    @field:Min(value = 1, message = "Table number must be at least 1")
    @JsonProperty("tableNumber")
    val tableNumber: Int,

    @field:Min(value = 1, message = "Capacity must be at least 1")
    @JsonProperty("capacity")
    val capacity: Int? = null,

    @JsonProperty("isActive")
    val isActive: Boolean = true
)
