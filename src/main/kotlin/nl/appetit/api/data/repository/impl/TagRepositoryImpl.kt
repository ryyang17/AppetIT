package nl.appetit.api.data.repository.impl

import nl.appetit.api.data.mapper.TagMapper
import nl.appetit.api.data.repository.TagR2dbcRepository
import nl.appetit.api.logic.model.Tag
import nl.appetit.api.logic.repository.TagRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class TagRepositoryImpl(
    private val db: TagR2dbcRepository
) : TagRepository {
    override fun findAll(): Flux<Tag> =
        db.findAll().map(TagMapper::toModel)

    override fun findById(id: Int): Mono<Tag> =
        db.findById(id).map(TagMapper::toModel)

    override fun save(tag: Tag): Mono<Tag> =
        db.save(TagMapper.toEntity(tag)).map(TagMapper::toModel)

    override fun update(id: Int, tag: Tag): Mono<Tag> =
        findById(id)
            .switchIfEmpty(Mono.error(RuntimeException("Tag not found with id: $id")))
            .flatMap { existing ->
                val updated = existing.copy(
                    name = tag.name,
                    svgIcon = tag.svgIcon,
                    updatedAt = java.time.Instant.now()
                )
                save(updated)
            }

    override fun deleteById(id: Int): Mono<Void> =
        db.deleteById(id)
}
