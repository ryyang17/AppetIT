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

	fun instertCategory(category: Category): Mono<Category> =
		db.save(category)
}
