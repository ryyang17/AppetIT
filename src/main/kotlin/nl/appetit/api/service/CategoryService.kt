package nl.appetit.api.service

import nl.appetit.api.dto.CategoryDTO
import nl.appetit.api.dto.toDTO
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
            .sort { c1, c2 -> (c1.order ?: 0).compareTo(c2.order ?: 0) }

    /**
     * Haalt alle categorieën op en bouwt een hierarchische boom-structuur
     * Alleen root categorieën (parentId = null) worden geretourneerd met hun children
     */
    fun getCategoriesHierarchical(): Mono<List<CategoryDTO>> {
        return db.findAll()
            .collectList()
            .map { categories ->
                buildCategoryTree(categories)
            }
    }

    /**
     * Bouwt een hierarchische boom van categorieën
     * Efficiënt algoritme: O(n) tijd complexiteit
     */
    private fun buildCategoryTree(categories: List<Category>): List<CategoryDTO> {
        // Maak een map voor snelle lookup van categorieën per ID
        val categoryMap = categories.associateBy { it.id!! }
        
        // Groepeer categorieën per parentId voor efficiënte children lookup
        val childrenMap = categories
            .filter { it.parentId != null }
            .groupBy { it.parentId }
        
        // Recursieve functie om children te bouwen
        fun buildChildren(parentId: Long): List<CategoryDTO> {
            return childrenMap[parentId]?.map { child ->
                child.toDTO(
                    children = child.id?.let { buildChildren(it) } ?: emptyList()
                )
            } ?: emptyList()
        }
        
        // Vind root categorieën (zonder parent) en bouw hun subtrees
        return categories
            .filter { it.parentId == null }
            .map { root ->
                root.toDTO(
                    children = root.id?.let { buildChildren(it) } ?: emptyList()
                )
            }
            .sortedBy { it.name } // Sorteer alfabetisch
    }

    fun insertCategory(category: Category): Mono<Category> =
        db.save(category)

    fun deleteCategoryById(id: Long): Mono<Void> =
        db.deleteById(id)

    fun updateCategory(id: Long, newCategory: Category): Mono<Category> =
        db.findById(id)
            .switchIfEmpty(Mono.error(RuntimeException("Category not found with id: $id")))
            .flatMap { existing ->
                val updated = existing.copy(
                    name = newCategory.name,
                    parentId = newCategory.parentId
                )
                db.save(updated)
            }

    fun updateCategoryOrder(ids: List<Long>): Mono<Void> {
        return Flux.fromIterable(ids.withIndex())
            .flatMap { (order, id) ->
                db.findById(id)
                    .flatMap { category ->
                        db.save(category.copy(order = order))
                    }
            }
            .then()
    }

    fun getSubcategoriesByParentId(parentId: Long): Flux<Category> =
        db.findByParentId(parentId)

}
