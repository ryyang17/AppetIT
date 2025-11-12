package nl.appetit.api.presentation.dto.tag

data class TagRequest(
    val name: String,
    val svgIcon: String? = null
)


