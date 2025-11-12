package nl.appetit.api.logic.model

import java.time.LocalDateTime

data class Restaurant(
    val id: Int? = null,
    val name: String,
    val address: String,
    val phone: String,
    val email: String,
    val isActive: Boolean,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
