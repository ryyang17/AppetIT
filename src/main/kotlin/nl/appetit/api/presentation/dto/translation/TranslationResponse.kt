package nl.appetit.api.presentation.dto.translation

/**
 * Response DTO used by the frontend when reading translations.
 */
data class TranslationResponse(
    val translationId: Long,
    val entityType: String,
    val entityId: Long,
    val language: String,
    val translation: String?
)
