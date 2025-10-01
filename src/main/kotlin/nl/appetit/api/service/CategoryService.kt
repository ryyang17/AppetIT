package nl.appetit.api.service

import nl.appetit.api.model.Category
import nl.appetit.api.repository.CategoryRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CategoryService(
    private val db: CategoryRepository
) {
    fun getAllCategories(): Flux<Category> = 
        db.findAll()

    fun insertCategory(category: Category): Mono<Category> =
        db.save(category)

    fun deleteCategoryById(id: Long): Mono<Void> =
        db.deleteById(id)

    fun updateCategory(id: Long, newCategory: Category): Mono<Category> =
        db.findById(id)
            .switchIfEmpty(Mono.error(RuntimeException("Category not found with id: $id")))
            .flatMap { existing ->
                val updated = existing.copy(
                    name = newCategory.name
                )
                db.save(updated)
            }




}
