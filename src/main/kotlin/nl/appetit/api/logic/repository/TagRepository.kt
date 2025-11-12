package nl.appetit.api.logic.repository

import nl.appetit.api.logic.model.Tag
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TagRepository {
    fun findAll(): Flux<Tag>
    fun findById(id: Int): Mono<Tag>
    fun save(tag: Tag): Mono<Tag>
    fun update(id: Int, tag: Tag): Mono<Tag>
    fun deleteById(id: Int): Mono<Void>
}
