package nl.appetit.api.presentation.dto.translation

data class TranslationResponse(
    val translationId: Long,
    val entityType: String,
    val entityId: Long,
    val language: String,
    val translation: String?
)
