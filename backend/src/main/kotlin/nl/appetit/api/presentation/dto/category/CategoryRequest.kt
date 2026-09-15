package nl.appetit.api.presentation.dto.category

data class CategoryRequest(
    val name: String,
    val parentId: Long? = null
)
