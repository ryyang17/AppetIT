package nl.appetit.api.data.mapper

import nl.appetit.api.data.entity.EmployeeEntity
import nl.appetit.api.logic.model.Employee
import nl.appetit.api.logic.model.EmployeeRole

object EmployeeMapper {
    fun toModel(source: EmployeeEntity) = Employee(
        id = source.id,
        personnelNumber = source.personnelNumber,
        restaurantId = source.restaurantId,
        firstName = source.firstName,
        lastName = source.lastName,
        role = try {
            EmployeeRole.valueOf(source.role)
        } catch (_: Exception) {
            EmployeeRole.MANAGER
        },
        active = source.active,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt
    )

    fun toEntity(source: Employee) = EmployeeEntity(
        id = source.id,
        personnelNumber = source.personnelNumber,
        restaurantId = source.restaurantId,
        firstName = source.firstName,
        lastName = source.lastName,
        role = source.role.name,
        active = source.active,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt
    )
}
