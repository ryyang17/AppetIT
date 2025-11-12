package nl.appetit.api.presentation.dto.tag

import java.time.Instant

data class TagResponse(
    val id: Int,
    val name: String,
    val svgIcon: String? = null,
    val createdAt: Instant?,
    val updatedAt: Instant?
)


