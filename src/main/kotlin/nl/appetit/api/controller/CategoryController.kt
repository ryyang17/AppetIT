package nl.appetit.api.controller

import nl.appetit.api.model.Category
import nl.appetit.api.repository.CategoryRepository
import nl.appetit.api.service.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

data class CategoryRequest(
	val name: String
)

fun CategoryRequest.toEntity() = Category(
	name = this.name
)

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val service: CategoryService
) {

    @GetMapping
    fun list(): Flux<Category> = service.getAllCategories()

	@PostMapping
    fun insert(@RequestBody request: Mono<CategoryRequest>): Mono<Category> {
        return request.flatMap { category ->
            service.insertCategory(category.toEntity())
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): Mono<Void> =
        service.deleteCategoryById(id)
}
