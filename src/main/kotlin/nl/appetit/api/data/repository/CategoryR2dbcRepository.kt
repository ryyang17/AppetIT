package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.CategoryEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface CategoryR2dbcRepository : ReactiveCrudRepository<CategoryEntity, Long> {
    fun findByParentId(parentId: Long): Flux<CategoryEntity>
}
