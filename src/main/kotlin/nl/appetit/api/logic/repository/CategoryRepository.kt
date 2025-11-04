package nl.appetit.api.logic.repository

import nl.appetit.api.data.entity.CategoryEntity
import nl.appetit.api.logic.model.Category
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CategoryRepository {
    fun findByParentId(parentId: Long): Flux<Category>
    fun findById(id: Long): Mono<Category>
    fun findAll(): Flux<Category>
    fun save(category: Category): Mono<Category>
    fun deleteById(id: Long): Mono<Void>
}