package nl.appetit.api.presentation.mapper

import nl.appetit.api.logic.model.Employee
import nl.appetit.api.logic.model.EmployeeRole
import nl.appetit.api.presentation.dto.employee.EmployeeRequest
import nl.appetit.api.presentation.dto.employee.EmployeeResponse

object EmployeeMapper {

    fun Employee.toResponse() = EmployeeResponse(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        role = this.role,
        personnelNumber = this.personnelNumber,
        restaurantId = this.restaurantId,
        active = this.active,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

    fun EmployeeRequest.toModel(): Employee {
        val parsedRole = try {
            EmployeeRole.valueOf(this.role.trim().uppercase())
        } catch (_: Exception) {
            throw IllegalArgumentException("Invalid role: ${this.role}")
        }
        return Employee(
            firstName = this.firstName,
            lastName = this.lastName,
            role = parsedRole,
            personnelNumber = this.personnelNumber,
            restaurantId = this.restaurantId,
            active = this.active
        )
    }
}