package nl.appetit.api.presentation.dto.category

import java.time.Instant

/**
 * DTO voor Category responses met hierarchische ondersteuning
 */
data class CategoryTreeResponse(
    val id: Long,
    val name: String,
    val parentId: Long? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
    val children: List<CategoryTreeResponse> = emptyList()
)


