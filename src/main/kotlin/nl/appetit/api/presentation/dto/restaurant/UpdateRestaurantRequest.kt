package nl.appetit.api.presentation.dto.restaurant

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateRestaurantRequest(
    @field:Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    val name: String?,

    val address: String?,

    @field:Size(min = 1, max = 50, message = "Phone must be between 1 and 50 characters")
    val phone: String?,

    @field:Size(min = 1, max = 255, message = "Email must be between 1 and 255 characters")
    val email: String?,

    @JsonProperty("is_active")
    val active: Boolean?,
)
