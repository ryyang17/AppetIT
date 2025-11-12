package nl.appetit.api.logic.service

import nl.appetit.api.logic.model.Product
import nl.appetit.api.logic.repository.ProductRepository
import nl.appetit.api.logic.repository.ProductTagRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.math.BigDecimal
import java.time.Instant

class ProductServiceTest {

    private lateinit var productRepository: ProductRepository
    private lateinit var productTagRepository: ProductTagRepository
    private lateinit var productService: ProductService

    @BeforeEach
    fun setUp() {
        productRepository = mock()
        productTagRepository = mock()
        productService = ProductService(productRepository, productTagRepository)
    }

    @Test
    fun `findAll should return all products`() {
        // Arrange
        val products = listOf(
            createProduct(1, "Product 1"),
            createProduct(2, "Product 2")
        )
        whenever(productRepository.findAll()).thenReturn(Flux.fromIterable(products))

        // Act
        val result = productService.findAll()

        // Assert
        StepVerifier.create(result)
            .expectNext(products[0])
            .expectNext(products[1])
            .verifyComplete()

        verify(productRepository, times(1)).findAll()
    }

    @Test
    fun `save should return saved product`() {
        // Arrange
        val product = createProduct(null, "New Product")
        val savedProduct = createProduct(1, "New Product")
        whenever(productRepository.save(any())).thenReturn(Mono.just(savedProduct))

        // Act
        val result = productService.save(product)

        // Assert
        StepVerifier.create(result)
            .expectNext(savedProduct)
            .verifyComplete()

        verify(productRepository, times(1)).save(product)
    }

    @Test
    fun `deleteById should delete product`() {
        // Arrange
        val productId = 1
        whenever(productRepository.deleteById(productId)).thenReturn(Mono.empty())

        // Act
        val result = productService.deleteById(productId)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(productRepository, times(1)).deleteById(productId)
    }

    @Test
    fun `deleteAll should delete all products`() {
        // Arrange
        whenever(productRepository.deleteAll()).thenReturn(Mono.empty())

        // Act
        val result = productService.deleteAll()

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(productRepository, times(1)).deleteAll()
    }

    @Test
    fun `update should update existing product`() {
        // Arrange
        val productId = 1
        val existingProduct = createProduct(productId, "Old Name")
        val updatedProduct = createProduct(productId, "New Name")
        
        whenever(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct))
        whenever(productRepository.save(any())).thenReturn(Mono.just(updatedProduct))

        // Act
        val result = productService.update(productId, updatedProduct)

        // Assert
        StepVerifier.create(result)
            .expectNext(updatedProduct)
            .verifyComplete()

        verify(productRepository, times(1)).findById(productId)
        verify(productRepository, times(1)).save(any())
    }

    @Test
    fun `update should throw error when product not found`() {
        // Arrange
        val productId = 999
        val productToUpdate = createProduct(productId, "New Name")
        whenever(productRepository.findById(productId)).thenReturn(Mono.empty())

        // Act
        val result = productService.update(productId, productToUpdate)

        // Assert
        StepVerifier.create(result)
            .expectError(RuntimeException::class.java)
            .verify()

        verify(productRepository, times(1)).findById(productId)
        verify(productRepository, never()).save(any())
    }

    @Test
    fun `findAllByCategoryId should return products for category`() {
        // Arrange
        val categoryId = 1
        val products = listOf(
            createProduct(1, "Product 1", categoryId),
            createProduct(2, "Product 2", categoryId)
        )
        whenever(productRepository.findAllByCategoryId(categoryId))
            .thenReturn(Flux.fromIterable(products))

        // Act
        val result = productService.findAllByCategoryId(categoryId)

        // Assert
        StepVerifier.create(result)
            .expectNext(products[0])
            .expectNext(products[1])
            .verifyComplete()

        verify(productRepository, times(1)).findAllByCategoryId(categoryId)
    }

    @Test
    fun `findAllExcludingTagIds should return all products when tagIds is empty`() {
        // Arrange
        val products = listOf(
            createProduct(1, "Product 1"),
            createProduct(2, "Product 2")
        )
        whenever(productRepository.findAll()).thenReturn(Flux.fromIterable(products))

        // Act
        val result = productService.findAllExcludingTagIds(emptyList())

        // Assert
        StepVerifier.create(result)
            .expectNext(products[0])
            .expectNext(products[1])
            .verifyComplete()

        verify(productRepository, times(1)).findAll()
        verify(productTagRepository, never()).findProductsExcludingTagIds(any())
    }

    @Test
    fun `findAllExcludingTagIds should return filtered products when tagIds provided`() {
        // Arrange
        val tagIds = listOf(1, 2)
        val filteredProducts = listOf(
            createProduct(3, "Product 3"), // Product without tags 1 or 2
            createProduct(4, "Product 4")  // Product without tags 1 or 2
        )
        whenever(productTagRepository.findProductsExcludingTagIds(tagIds))
            .thenReturn(Flux.fromIterable(filteredProducts))

        // Act
        val result = productService.findAllExcludingTagIds(tagIds)

        // Assert
        StepVerifier.create(result)
            .expectNext(filteredProducts[0])
            .expectNext(filteredProducts[1])
            .verifyComplete()

        verify(productTagRepository, times(1)).findProductsExcludingTagIds(tagIds)
        verify(productRepository, never()).findAll()
    }

    @Test
    fun `findAllExcludingTagIds should exclude products with any of the specified tags`() {
        // Arrange
        val tagIds = listOf(1) // Exclude products with tag 1
        val filteredProducts = listOf(
            createProduct(2, "Product without tag 1"),
            createProduct(3, "Another product without tag 1")
        )
        whenever(productTagRepository.findProductsExcludingTagIds(tagIds))
            .thenReturn(Flux.fromIterable(filteredProducts))

        // Act
        val result = productService.findAllExcludingTagIds(tagIds)

        // Assert
        StepVerifier.create(result)
            .expectNext(filteredProducts[0])
            .expectNext(filteredProducts[1])
            .verifyComplete()

        verify(productTagRepository, times(1)).findProductsExcludingTagIds(tagIds)
    }

    @Test
    fun `findAllExcludingTagIds should handle multiple tag exclusions`() {
        // Arrange
        val tagIds = listOf(1, 2, 3) // Exclude products with any of these tags
        val filteredProducts = listOf(
            createProduct(4, "Product without tags 1, 2, or 3")
        )
        whenever(productTagRepository.findProductsExcludingTagIds(tagIds))
            .thenReturn(Flux.fromIterable(filteredProducts))

        // Act
        val result = productService.findAllExcludingTagIds(tagIds)

        // Assert
        StepVerifier.create(result)
            .expectNext(filteredProducts[0])
            .verifyComplete()

        verify(productTagRepository, times(1)).findProductsExcludingTagIds(tagIds)
    }

    private fun createProduct(
        id: Int?,
        name: String,
        categoryId: Int? = null
    ): Product {
        return Product(
            id = id,
            name = name,
            price = BigDecimal("10.00"),
            description = "Description",
            imageUrl = "https://example.com/image.jpg",
            isAvailable = true,
            categoryId = categoryId,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
