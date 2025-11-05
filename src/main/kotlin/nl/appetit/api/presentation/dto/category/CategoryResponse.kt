package nl.appetit.api.presentation.dto.category

import java.time.Instant

data class CategoryResponse(
    val id: Long,
    val name: String,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
    val parentId: Long? = null,
    val order: Int? = null
)
