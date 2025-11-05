package nl.appetit.api.presentation.controller

import nl.appetit.api.presentation.dto.category.CategoryTreeResponse
import nl.appetit.api.logic.service.CategoryService
import nl.appetit.api.presentation.dto.category.CategoryRequest
import nl.appetit.api.presentation.dto.category.CategoryResponse
import nl.appetit.api.presentation.dto.category.MoveCategoryRequest
import nl.appetit.api.presentation.mapper.CategoryMapper
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
    fun list(): Flux<CategoryResponse> = service.getAllCategories()
        .map(CategoryMapper::toResponse)

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
    fun getTree(): Mono<List<CategoryTreeResponse>> = service.getCategoriesHierarchical()
        .map { categories -> categories.map(CategoryMapper::toTreeResponse) }

	@PostMapping
    fun insert(@RequestBody request: Mono<CategoryRequest>): Mono<CategoryResponse> {
        return request.flatMap { category ->
            service.insertCategory(CategoryMapper.toModel(category))
                .map(CategoryMapper::toResponse)
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): Mono<Void> =
        service.deleteCategoryById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: Mono<CategoryRequest>): Mono<CategoryResponse> =
        request.flatMap { category ->
            service.updateCategory(id, CategoryMapper.toModel(category))
                .map(CategoryMapper::toResponse)
        }

    @PutMapping("/move")
    fun moveCategory(@RequestBody request: Mono<MoveCategoryRequest>): Mono<CategoryResponse> {
        return request.flatMap { moveReq ->
            service.moveCategory(moveReq.categoryId, moveReq.newParentId)
                .map(CategoryMapper::toResponse)
        }
    }

    @GetMapping("/subcategory/{parentId}")
    fun getSubcategories(@PathVariable parentId: Long): Flux<CategoryResponse> =
        service.getSubcategoriesByParentId(parentId)
            .map(CategoryMapper::toResponse)
}
