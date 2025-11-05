package nl.appetit.api.presentation

import nl.appetit.api.logic.model.Employee
import nl.appetit.api.logic.model.EmployeeRole

// Data class for API responses

data class EmployeeResponse(
    val id: Int?,
    val firstName: String,
    val lastName: String,
    val role: EmployeeRole,
    val personnelNumber: String?,
    val restaurantId: Int?,
    val active: Boolean
)

fun Employee.toResponse() = EmployeeResponse(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    role = this.role,
    personnelNumber = this.personnelNumber,
    restaurantId = this.restaurantId,
    active = this.active
)

