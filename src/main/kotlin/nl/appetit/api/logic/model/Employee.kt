package nl.appetit.api.logic.model

import java.time.Instant

data class Employee(
    val id: Int? = null,
    val personnelNumber: String? = null,
    val restaurantId: Int? = null,
    val firstName: String,
    val lastName: String,
    val role: EmployeeRole,
    val active: Boolean = true,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)
