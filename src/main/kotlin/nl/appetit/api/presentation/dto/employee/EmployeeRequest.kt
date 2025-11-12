package nl.appetit.api.presentation.dto.employee

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

// This prevents errors if the frontend sends extra fields that are not defined in this data class.
@JsonIgnoreProperties(ignoreUnknown = true)

data class EmployeeRequest(
    val firstName: String,
    val lastName: String,
    val role: String,
    val personnelNumber: Int? = null,
    val restaurantId: Int? = null,
    val active: Boolean = true
)
