package nl.appetit.api.presentation.dto.category

data class MoveCategoryRequest(
    val categoryId: Long,
    val newParentId: Long?
)
