package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.TranslationEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TranslationR2dbcRepository : ReactiveCrudRepository<TranslationEntity, Long> {
    fun findByEntityTypeAndEntityId(entityType: String, entityId: Long): Flux<TranslationEntity>
    fun findByEntityTypeAndEntityIdAndLanguage(entityType: String, entityId: Long, language: String): Mono<TranslationEntity>
    fun findByLanguage(language: String): Flux<TranslationEntity>
}