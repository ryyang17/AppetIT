package nl.appetit.api.presentation.mapper

import nl.appetit.api.logic.model.Employee
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
        active = this.active
    )

    fun EmployeeRequest.toModel() = Employee(
        firstName = this.firstName,
        lastName = this.lastName,
        role = this.role,
        personnelNumber = this.personnelNumber,
        restaurantId = this.restaurantId,
        active = this.active
    )
}