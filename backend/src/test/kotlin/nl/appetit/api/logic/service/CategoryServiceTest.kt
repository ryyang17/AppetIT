package nl.appetit.api.logic.service

import nl.appetit.api.logic.model.Category
import nl.appetit.api.logic.model.TreeCategory
import nl.appetit.api.logic.repository.CategoryRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Instant

class CategoryServiceTest {

    private lateinit var categoryRepository: CategoryRepository
    private lateinit var categoryService: CategoryService

    @BeforeEach
    fun setUp() {
        categoryRepository = mock()
        categoryService = CategoryService(categoryRepository)
    }

    @Test
    fun `getAllCategories should return all categories sorted by order`() {
        // Arrange
        val categories = listOf(
            createCategory(1, "Category B", order = 2),
            createCategory(2, "Category A", order = 1),
            createCategory(3, "Category C", order = 3)
        )
        whenever(categoryRepository.findAll())
            .thenReturn(Flux.fromIterable(categories))

        // Act
        val result = categoryService.getAllCategories()

        // Assert
        StepVerifier.create(result)
            .expectNext(categories[1]) // Category A (order 1)
            .expectNext(categories[0]) // Category B (order 2)
            .expectNext(categories[2]) // Category C (order 3)
            .verifyComplete()

        verify(categoryRepository).findAll()
    }

    @Test
    fun `getCategoriesHierarchical should return hierarchical tree structure`() {
        // Arrange
        val rootCategory = createCategory(1, "Food", parentId = null)
        val subCategory = createCategory(2, "Appetizers", parentId = 1L)
        val subSubCategory = createCategory(3, "Bread & Starters", parentId = 2L)
        
        whenever(categoryRepository.findAll())
            .thenReturn(Flux.fromIterable(listOf(rootCategory, subCategory, subSubCategory)))

        // Act
        val result = categoryService.getCategoriesHierarchical()

        // Assert
        StepVerifier.create(result)
            .expectNextMatches { treeCategories ->
                treeCategories.size == 1 &&
                treeCategories[0].name == "Food" &&
                treeCategories[0].children.size == 1 &&
                treeCategories[0].children[0].name == "Appetizers" &&
                treeCategories[0].children[0].children.size == 1 &&
                treeCategories[0].children[0].children[0].name == "Bread & Starters"
            }
            .verifyComplete()

        verify(categoryRepository).findAll()
    }

    @Test
    fun `getCategoriesHierarchical should return multiple root categories`() {
        // Arrange
        val foodCategory = createCategory(1, "Food", parentId = null)
        val beveragesCategory = createCategory(2, "Beverages", parentId = null)
        
        whenever(categoryRepository.findAll())
            .thenReturn(Flux.fromIterable(listOf(foodCategory, beveragesCategory)))

        // Act
        val result = categoryService.getCategoriesHierarchical()

        // Assert
        StepVerifier.create(result)
            .expectNextMatches { treeCategories ->
                treeCategories.size == 2 &&
                treeCategories.any { it.name == "Food" } &&
                treeCategories.any { it.name == "Beverages" }
            }
            .verifyComplete()
    }

    @Test
    fun `insertCategory should save and return category`() {
        // Arrange
        val category = createCategory(null, "New Category")
        val savedCategory = createCategory(1, "New Category")
        whenever(categoryRepository.save(any())).thenReturn(Mono.just(savedCategory))

        // Act
        val result = categoryService.insertCategory(category)

        // Assert
        StepVerifier.create(result)
            .expectNext(savedCategory)
            .verifyComplete()

        verify(categoryRepository).save(category)
    }

    @Test
    fun `deleteCategoryById should delete category`() {
        // Arrange
        val categoryId = 1L
        whenever(categoryRepository.deleteById(categoryId)).thenReturn(Mono.empty())

        // Act
        val result = categoryService.deleteCategoryById(categoryId)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(categoryRepository).deleteById(categoryId)
    }

    @Test
    fun `updateCategory should update existing category`() {
        // Arrange
        val categoryId = 1L
        val existingCategory = createCategory(categoryId, "Old Name")
        val updatedCategory = createCategory(categoryId, "New Name")
        
        whenever(categoryRepository.findById(categoryId))
            .thenReturn(Mono.just(existingCategory))
        whenever(categoryRepository.save(any())).thenReturn(Mono.just(updatedCategory))

        // Act
        val result = categoryService.updateCategory(categoryId, updatedCategory)

        // Assert
        StepVerifier.create(result)
            .expectNext(updatedCategory)
            .verifyComplete()

        verify(categoryRepository).findById(categoryId)
        verify(categoryRepository).save(any())
    }

    @Test
    fun `updateCategory should throw error when category not found`() {
        // Arrange
        val categoryId = 999L
        val categoryToUpdate = createCategory(categoryId, "New Name")
        whenever(categoryRepository.findById(categoryId)).thenReturn(Mono.empty())

        // Act
        val result = categoryService.updateCategory(categoryId, categoryToUpdate)

        // Assert
        StepVerifier.create(result)
            .expectError(RuntimeException::class.java)
            .verify()

        verify(categoryRepository).findById(categoryId)
        verify(categoryRepository, never()).save(any())
    }

    @Test
    fun `moveCategory should update parentId`() {
        // Arrange
        val categoryId = 1L
        val newParentId = 2L
        val existingCategory = createCategory(categoryId, "Category", parentId = null)
        val movedCategory = createCategory(categoryId, "Category", parentId = newParentId)
        
        whenever(categoryRepository.findById(categoryId))
            .thenReturn(Mono.just(existingCategory))
        whenever(categoryRepository.save(any())).thenReturn(Mono.just(movedCategory))

        // Act
        val result = categoryService.moveCategory(categoryId, newParentId)

        // Assert
        StepVerifier.create(result)
            .expectNextMatches { it.parentId == newParentId }
            .verifyComplete()

        verify(categoryRepository).findById(categoryId)
        verify(categoryRepository).save(any())
    }

    @Test
    fun `moveCategory should throw error when category not found`() {
        // Arrange
        val categoryId = 999L
        val newParentId = 2L
        whenever(categoryRepository.findById(categoryId)).thenReturn(Mono.empty())

        // Act
        val result = categoryService.moveCategory(categoryId, newParentId)

        // Assert
        StepVerifier.create(result)
            .expectError(RuntimeException::class.java)
            .verify()

        verify(categoryRepository).findById(categoryId)
        verify(categoryRepository, never()).save(any())
    }

    @Test
    fun `getSubcategoriesByParentId should return subcategories`() {
        // Arrange
        val parentId = 1L
        val subcategories = listOf(
            createCategory(2, "Subcategory 1", parentId = parentId),
            createCategory(3, "Subcategory 2", parentId = parentId)
        )
        whenever(categoryRepository.findByParentId(parentId))
            .thenReturn(Flux.fromIterable(subcategories))

        // Act
        val result = categoryService.getSubcategoriesByParentId(parentId)

        // Assert
        StepVerifier.create(result)
            .expectNext(subcategories[0])
            .expectNext(subcategories[1])
            .verifyComplete()

        verify(categoryRepository).findByParentId(parentId)
    }

    private fun createCategory(
        id: Long?,
        name: String,
        parentId: Long? = null,
        order: Int? = null
    ): Category {
        return Category(
            id = id,
            name = name,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            parentId = parentId,
            order = order
        )
    }
}
