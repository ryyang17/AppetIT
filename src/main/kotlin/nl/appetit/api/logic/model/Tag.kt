package nl.appetit.api.logic.model

import java.time.Instant

data class Tag(
    val id: Int? = null,
    val name: String,
    val svgIcon: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)
