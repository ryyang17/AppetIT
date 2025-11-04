package nl.appetit.api.data.mapper

import nl.appetit.api.data.entity.CategoryEntity
import nl.appetit.api.logic.model.Category

object CategoryMapper {
    fun toModel(source: CategoryEntity) = Category(
        id = source.id,
        name = source.name,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt,
        parentId = source.parentId,
        order = source.order,
    )

    fun toEntity(source: Category) = CategoryEntity(
        id = source.id,
        name = source.name,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt,
        parentId = source.parentId,
        order = source.order,
    )
}