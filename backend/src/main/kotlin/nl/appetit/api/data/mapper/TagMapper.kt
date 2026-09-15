package nl.appetit.api.data.mapper

import nl.appetit.api.data.entity.TagEntity
import nl.appetit.api.logic.model.Tag

object TagMapper {
    fun toModel(source: TagEntity) = Tag(
        id = source.id,
        name = source.name,
        svgIcon = source.svgIcon,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt,
    )

    fun toEntity(source: Tag) = TagEntity(
        id = source.id,
        name = source.name,
        svgIcon = source.svgIcon,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt,
    )
}
