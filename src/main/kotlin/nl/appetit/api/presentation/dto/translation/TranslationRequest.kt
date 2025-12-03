package nl.appetit.api.presentation.dto.translation

/**
 * Request DTO used by the admin app to create or update
 * a translation for a specific (entityType, entityId, language)
 * combination.
 */
data class TranslationRequest(
    val entityType: String,
    val entityId: Long,
    val language: String,
    val translation: String?
)


