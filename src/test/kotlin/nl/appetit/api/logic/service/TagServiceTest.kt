package nl.appetit.api.logic.service

import nl.appetit.api.logic.model.Product
import nl.appetit.api.logic.model.Tag
import nl.appetit.api.logic.repository.ProductRepository
import nl.appetit.api.logic.repository.ProductTagRepository
import nl.appetit.api.logic.repository.TagRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Instant

class TagServiceTest {

    private lateinit var tagRepository: TagRepository
    private lateinit var productTagRepository: ProductTagRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var tagService: TagService

    @BeforeEach
    fun setUp() {
        tagRepository = mock()
        productTagRepository = mock()
        productRepository = mock()
        tagService = TagService(tagRepository, productTagRepository, productRepository)
    }

    @Test
    fun `findAll should return all tags`() {
        // Arrange
        val tags = listOf(
            createTag(1, "Tag 1"),
            createTag(2, "Tag 2")
        )
        whenever(tagRepository.findAll()).thenReturn(Flux.fromIterable(tags))

        // Act
        val result = tagService.findAll()

        // Assert
        StepVerifier.create(result)
            .expectNext(tags[0])
            .expectNext(tags[1])
            .verifyComplete()

        verify(tagRepository).findAll()
    }

    @Test
    fun `findById should return tag when exists`() {
        // Arrange
        val tagId = 1
        val tag = createTag(tagId, "Tag 1")
        whenever(tagRepository.findById(tagId)).thenReturn(Mono.just(tag))

        // Act
        val result = tagService.findById(tagId)

        // Assert
        StepVerifier.create(result)
            .expectNext(tag)
            .verifyComplete()

        verify(tagRepository).findById(tagId)
    }

    @Test
    fun `findById should return empty when not exists`() {
        // Arrange
        val tagId = 999
        whenever(tagRepository.findById(tagId)).thenReturn(Mono.empty())

        // Act
        val result = tagService.findById(tagId)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(tagRepository).findById(tagId)
    }

    @Test
    fun `save should return saved tag`() {
        // Arrange
        val tag = createTag(null, "New Tag")
        val savedTag = createTag(1, "New Tag")
        whenever(tagRepository.save(any())).thenReturn(Mono.just(savedTag))

        // Act
        val result = tagService.save(tag)

        // Assert
        StepVerifier.create(result)
            .expectNext(savedTag)
            .verifyComplete()

        verify(tagRepository).save(tag)
    }

    @Test
    fun `update should update existing tag`() {
        // Arrange
        val tagId = 1
        val updatedTag = createTag(tagId, "Updated Tag")
        whenever(tagRepository.update(tagId, updatedTag))
            .thenReturn(Mono.just(updatedTag))

        // Act
        val result = tagService.update(tagId, updatedTag)

        // Assert
        StepVerifier.create(result)
            .expectNext(updatedTag)
            .verifyComplete()

        verify(tagRepository).update(tagId, updatedTag)
    }

    @Test
    fun `deleteById should delete tag`() {
        // Arrange
        val tagId = 1
        whenever(tagRepository.deleteById(tagId)).thenReturn(Mono.empty())

        // Act
        val result = tagService.deleteById(tagId)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(tagRepository).deleteById(tagId)
    }

    @Test
    fun `getTagsByProductId should return tags for product`() {
        // Arrange
        val productId = 1
        val tags = listOf(
            createTag(1, "Tag 1"),
            createTag(2, "Tag 2")
        )
        whenever(productTagRepository.findTagsByProductId(productId))
            .thenReturn(Flux.fromIterable(tags))

        // Act
        val result = tagService.getTagsByProductId(productId)

        // Assert
        StepVerifier.create(result)
            .expectNext(tags[0])
            .expectNext(tags[1])
            .verifyComplete()

        verify(productTagRepository).findTagsByProductId(productId)
    }

    @Test
    fun `addTagToProduct should add tag when product and tag exist and not already associated`() {
        // Arrange
        val productId = 1
        val tagId = 2
        val product = createProduct(productId)
        val tag = createTag(tagId, "Tag 1")
        
        whenever(productRepository.findById(productId)).thenReturn(Mono.just(product))
        whenever(tagRepository.findById(tagId)).thenReturn(Mono.just(tag))
        whenever(productTagRepository.existsByProductIdAndTagId(productId, tagId))
            .thenReturn(Mono.just(false))
        whenever(productTagRepository.addTagToProduct(productId, tagId))
            .thenReturn(Mono.empty())

        // Act
        val result = tagService.addTagToProduct(productId, tagId)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(productRepository).findById(productId)
        verify(tagRepository).findById(tagId)
        verify(productTagRepository).existsByProductIdAndTagId(productId, tagId)
        verify(productTagRepository).addTagToProduct(productId, tagId)
    }

    @Test
    fun `addTagToProduct should not add tag when already associated`() {
        // Arrange
        val productId = 1
        val tagId = 2
        val product = createProduct(productId)
        val tag = createTag(tagId, "Tag 1")
        
        whenever(productRepository.findById(productId)).thenReturn(Mono.just(product))
        whenever(tagRepository.findById(tagId)).thenReturn(Mono.just(tag))
        whenever(productTagRepository.existsByProductIdAndTagId(productId, tagId))
            .thenReturn(Mono.just(true))

        // Act
        val result = tagService.addTagToProduct(productId, tagId)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(productRepository).findById(productId)
        verify(tagRepository).findById(tagId)
        verify(productTagRepository).existsByProductIdAndTagId(productId, tagId)
        verify(productTagRepository, never()).addTagToProduct(productId, tagId)
    }

    @Test
    fun `addTagToProduct should throw error when product not found`() {
        // Arrange
        val productId = 999
        val tagId = 2
        
        whenever(productRepository.findById(productId)).thenReturn(Mono.empty())
        // Mock the next operations in the chain to prevent NPE when .then() executes
        whenever(tagRepository.findById(any())).thenReturn(Mono.empty())
        whenever(productTagRepository.existsByProductIdAndTagId(any(), any()))
            .thenReturn(Mono.just(false))

        // Act
        val result = tagService.addTagToProduct(productId, tagId)

        // Assert
        StepVerifier.create(result)
            .expectErrorMatches { 
                it is RuntimeException && it.message?.contains("Product not found") == true 
            }
            .verify()

        verify(productRepository, times(1)).findById(productId)
        // Note: Due to .then() behavior, tagRepository.findById might still be called
        // but the error should propagate correctly
    }

    @Test
    fun `addTagToProduct should throw error when tag not found`() {
        // Arrange
        val productId = 1
        val tagId = 999
        val product = createProduct(productId)
        
        whenever(productRepository.findById(productId)).thenReturn(Mono.just(product))
        whenever(tagRepository.findById(tagId)).thenReturn(Mono.empty())
        // Mock the next operation to prevent NPE
        whenever(productTagRepository.existsByProductIdAndTagId(any(), any()))
            .thenReturn(Mono.just(false))

        // Act
        val result = tagService.addTagToProduct(productId, tagId)

        // Assert
        StepVerifier.create(result)
            .expectErrorMatches { 
                it is RuntimeException && it.message?.contains("Tag not found") == true 
            }
            .verify()

        verify(productRepository, times(1)).findById(productId)
        verify(tagRepository, times(1)).findById(tagId)
    }

    @Test
    fun `removeTagFromProduct should remove tag from product`() {
        // Given
        val productId = 1
        val tagId = 2
        whenever(productTagRepository.removeTagFromProduct(productId, tagId))
            .thenReturn(Mono.empty())

        // When
        val result = tagService.removeTagFromProduct(productId, tagId)

        // Then
        StepVerifier.create(result)
            .verifyComplete()

        verify(productTagRepository).removeTagFromProduct(productId, tagId)
    }

    private fun createTag(id: Int?, name: String): Tag {
        return Tag(
            id = id,
            name = name,
            svgIcon = "<svg></svg>",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    private fun createProduct(id: Int): Product {
        return Product(
            id = id,
            name = "Product $id",
            price = java.math.BigDecimal("10.00"),
            description = "Description",
            imageUrl = "https://example.com/image.jpg",
            isAvailable = true,
            categoryId = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
