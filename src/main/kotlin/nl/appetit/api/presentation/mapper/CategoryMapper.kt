package nl.appetit.api.presentation.mapper

import nl.appetit.api.logic.model.Category
import nl.appetit.api.logic.model.TreeCategory
import nl.appetit.api.presentation.dto.category.CategoryRequest
import nl.appetit.api.presentation.dto.category.CategoryResponse
import nl.appetit.api.presentation.dto.category.CategoryTreeResponse

object CategoryMapper {
    fun toTreeResponse(source: TreeCategory): CategoryTreeResponse =
        CategoryTreeResponse(
            id = source.id,
            name = source.name,
            parentId = source.parentId,
            createdAt = source.createdAt,
            updatedAt = source.updatedAt,
            children = source.children.map(::toTreeResponse)
        )

    fun toResponse(source: Category) = CategoryResponse(
        id = source.id ?: throw IllegalStateException("Category must have an ID"),
        name = source.name,
        parentId = source.parentId,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt,
        order = source.order
    )

    fun toModel(source: CategoryRequest) = Category(
        name = source.name,
        parentId = source.parentId
    )
}