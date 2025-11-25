package nl.appetit.api.presentation.controller

import nl.appetit.api.data.repository.TranslationR2dbcRepository
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
     * GET /translations - Get all translations
     */
    @GetMapping
    fun getAllTranslations(): Flux<TranslationResponse> {
        return translationRepository.findAll()
            .map { it.toResponse() }
    }

    /**
     * GET /translations/language/{language} - Get all translations for a specific language
     * Example: GET /translations/language/nl
     */
    @GetMapping("/language/{language}")
    fun getTranslationsByLanguage(@PathVariable language: String): Flux<TranslationResponse> {
        return translationRepository.findByLanguage(language)
            .map { it.toResponse() }
    }

    /**
     * GET /translations/{entityType}/{entityId} - Get all translations for a specific entity
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
     * GET /translations/{entityType}/{entityId}/{language} - Get translation for specific entity and language
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

    private fun nl.appetit.api.data.entity.TranslationEntity.toResponse() = TranslationResponse(
        translationId = id ?: 0,
        entityType = entityType,
        entityId = entityId,
        language = language,
        translation = translation
    )
}
