package nl.appetit.api.logic.dto

import nl.appetit.api.data.entity.CategoryEntity
import nl.appetit.api.logic.model.Category
import java.time.Instant

/**
 * DTO voor Category responses met hierarchische ondersteuning
 */
data class CategoryDTO(
    val id: Long,
    val name: String,
    val parentId: Long? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
    val children: List<CategoryDTO> = emptyList()
)

/**
 * Extension function om Category entity naar DTO te converteren
 */
fun Category.toDTO(children: List<CategoryDTO> = emptyList()): CategoryDTO {
    return CategoryDTO(
        id = this.id ?: throw IllegalStateException("Category must have an ID"),
        name = this.name,
        parentId = this.parentId,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        children = children
    )
}

