package nl.appetit.api.presentation.mapper

import nl.appetit.api.logic.model.Tag
import nl.appetit.api.presentation.dto.tag.TagRequest
import nl.appetit.api.presentation.dto.tag.TagResponse

object TagMapper {
    fun toModel(source: TagRequest) = Tag(
        name = source.name,
        svgIcon = source.svgIcon
    )

    fun toResponse(source: Tag) = TagResponse(
        id = source.id ?: throw IllegalStateException("Tag must have an ID"),
        name = source.name,
        svgIcon = source.svgIcon,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt
    )
}


