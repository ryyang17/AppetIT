package nl.appetit.api.presentation.dto.employee

import nl.appetit.api.logic.model.EmployeeRole
import java.time.Instant

data class EmployeeResponse(
    val id: Int?,
    val firstName: String,
    val lastName: String,
    val role: EmployeeRole,
    val personnelNumber: Int?,
    val restaurantId: Int?,
    val active: Boolean,
    val createdAt: Instant?,
    val updatedAt: Instant?
)
