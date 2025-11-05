package nl.appetit.api.presentation

import nl.appetit.api.logic.model.Employee
import nl.appetit.api.logic.model.EmployeeRole

data class EmployeeRequest(
    val firstName: String,
    val lastName: String,
    val role: EmployeeRole,
    val personnelNumber: String? = null,
    val restaurantId: Int? = null,
    val active: Boolean = true
)

fun EmployeeRequest.toModel() = Employee(
    firstName = this.firstName,
    lastName = this.lastName,
    role = this.role,
    personnelNumber = this.personnelNumber,
    restaurantId = this.restaurantId,
    active = this.active
)
