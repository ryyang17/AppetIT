package nl.appetit.api.controller

import nl.appetit.api.dto.CategoryDTO
import nl.appetit.api.model.Category
import nl.appetit.api.repository.CategoryRepository
import nl.appetit.api.service.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

data class CategoryRequest(
	val name: String,
    val parentId: Long? = null
)

fun CategoryRequest.toEntity() = Category(
	name = this.name,
    parentId = this.parentId
)

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val service: CategoryService
) {

    /**
     * GET /categories - Retourneert alle categorieën (flat list)
     * Gebruik dit voor backwards compatibility of eenvoudige lijsten
     */
    @GetMapping
    fun list(): Flux<Category> = service.getAllCategories()

    /**
     * GET /categories/tree - Retourneert hierarchische boom-structuur
     * Root categorieën bevatten hun children recursief
     * 
     * Response format:
     * [
     *   {
     *     "id": 1,
     *     "name": "Food",
     *     "parentId": null,
     *     "children": [
     *       {
     *         "id": 2,
     *         "name": "Pizza",
     *         "parentId": 1,
     *         "children": [...]
     *       }
     *     ]
     *   }
     * ]
     */
    @GetMapping("/tree")
    fun getTree(): Mono<List<CategoryDTO>> = service.getCategoriesHierarchical()

	@PostMapping
    fun insert(@RequestBody request: Mono<CategoryRequest>): Mono<Category> {
        return request.flatMap { category ->
            service.insertCategory(category.toEntity())
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): Mono<Void> =
        service.deleteCategoryById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: Mono<CategoryRequest>): Mono<Category> =
        request.flatMap { category ->
            service.updateCategory(id, category.toEntity())
        }

    @PutMapping("/CategoryOrder")
    fun updateOrder(@RequestBody ids: List<Long>): Mono<Void> {
        return service.updateCategoryOrder(ids)
    }

    @GetMapping("/subcategory/{parentId}")
    fun getSubcategories(@PathVariable parentId: Long): Flux<Category> =
        service.getSubcategoriesByParentId(parentId)
}
