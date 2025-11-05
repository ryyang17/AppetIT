package nl.appetit.api.presentation.dto.employee

import nl.appetit.api.logic.model.EmployeeRole

data class EmployeeRequest(
    val firstName: String,
    val lastName: String,
    val role: EmployeeRole,
    val personnelNumber: String? = null,
    val restaurantId: Int? = null,
    val active: Boolean = true
)


