package nl.appetit.api.presentation.dto.employee

import nl.appetit.api.logic.model.EmployeeRole

data class EmployeeResponse(
    val id: Int?,
    val firstName: String,
    val lastName: String,
    val role: EmployeeRole,
    val personnelNumber: String?,
    val restaurantId: Int?,
    val active: Boolean
)



