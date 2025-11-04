package nl.appetit.api.data.repository.impl

import nl.appetit.api.data.mapper.CategoryMapper
import nl.appetit.api.data.repository.CategoryR2dbcRepository
import nl.appetit.api.logic.model.Category
import nl.appetit.api.logic.repository.CategoryRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class CategoryRepositoryImpl(
    private val db: CategoryR2dbcRepository
) : CategoryRepository {
    override fun findByParentId(parentId: Long): Flux<Category> =
        db.findByParentId(parentId)
            .map(CategoryMapper::toModel)

    override fun findById(id: Long): Mono<Category> =
        db.findById(id)
            .map(CategoryMapper::toModel)

    override fun findAll(): Flux<Category> =
        db.findAll()
            .map(CategoryMapper::toModel)

    override fun save(category: Category): Mono<Category> =
        db.save(CategoryMapper.toEntity(category))
            .map(CategoryMapper::toModel)

    override fun deleteById(id: Long): Mono<Void> =
        db.deleteById(id)
}