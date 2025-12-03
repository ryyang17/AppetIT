package nl.appetit.api.presentation.controller

import nl.appetit.api.data.entity.TranslationEntity
import nl.appetit.api.data.repository.TranslationR2dbcRepository
import nl.appetit.api.presentation.dto.translation.TranslationRequest
import nl.appetit.api.presentation.dto.translation.TranslationResponse
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/translations")
class TranslationController(
    private val translationRepository: TranslationR2dbcRepository
) {

    /**
     * GET /translations - Get all translations.
     */
    @GetMapping
    fun getAllTranslations(): Flux<TranslationResponse> {
        return translationRepository.findAll()
            .map { it.toResponse() }
    }

    /**
     * GET /translations/language/{language} - Get all translations for a specific language.
     * Example: GET /translations/language/nl
     */
    @GetMapping("/language/{language}")
    fun getTranslationsByLanguage(@PathVariable language: String): Flux<TranslationResponse> {
        return translationRepository.findByLanguage(language)
            .map { it.toResponse() }
    }

    /**
     * GET /translations/{entityType}/{entityId} - Get all translations for a specific entity.
     * Example: GET /translations/category/1
     */
    @GetMapping("/{entityType}/{entityId}")
    fun getTranslationsForEntity(
        @PathVariable entityType: String,
        @PathVariable entityId: Long
    ): Flux<TranslationResponse> {
        return translationRepository.findByEntityTypeAndEntityId(entityType, entityId)
            .map { it.toResponse() }
    }

    /**
     * GET /translations/{entityType}/{entityId}/{language} - Get translation for specific entity and language.
     * Example: GET /translations/product/5/en
     */
    @GetMapping("/{entityType}/{entityId}/{language}")
    fun getTranslationForEntityAndLanguage(
        @PathVariable entityType: String,
        @PathVariable entityId: Long,
        @PathVariable language: String
    ): Mono<TranslationResponse> {
        return translationRepository.findByEntityTypeAndEntityIdAndLanguage(entityType, entityId, language)
            .map { it.toResponse() }
    }

    /**
     * POST /translations
     *
     * Upsert behaviour:
     * - Look up an existing row by (entityType, entityId, language).
     * - If it exists, update the `translation` field.
     * - If it does not exist, create a new TranslationEntity.
     *
     * This keeps the API simple for the admin front-end: it can always
     * call the same endpoint regardless of whether a translation already exists.
     */
    @PostMapping
    fun upsertTranslation(@RequestBody request: Mono<TranslationRequest>): Mono<TranslationResponse> {
        return request.flatMap { req ->
            translationRepository
                .findByEntityTypeAndEntityIdAndLanguage(req.entityType, req.entityId, req.language)
                // If not found, create a new entity as the starting point.
                .defaultIfEmpty(
                    TranslationEntity(
                        entityType = req.entityType,
                        entityId = req.entityId,
                        language = req.language,
                        translation = req.translation
                    )
                )
                // Whether existing or new, always overwrite the translation text.
                .map { existing ->
                    existing.copy(translation = req.translation)
                }
                // Persist the change and map back to response DTO.
                .flatMap { toSave -> translationRepository.save(toSave) }
                .map { it.toResponse() }
        }
    }

    private fun TranslationEntity.toResponse() = TranslationResponse(
        translationId = id ?: 0,
        entityType = entityType,
        entityId = entityId,
        language = language,
        translation = translation
    )
}
