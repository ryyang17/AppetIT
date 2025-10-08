package nl.appetit.api.repository

import nl.appetit.api.model.Category
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface CategoryRepository : ReactiveCrudRepository<Category, Long> {
    fun findByParentId(parentId: Long): Flux<Category>
}
