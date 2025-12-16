package nl.appetit.api.logic.service

import nl.appetit.api.data.entity.RestaurantProductEntity
import nl.appetit.api.data.repository.RestaurantProductR2dbcRepository
import nl.appetit.api.logic.model.Product
import nl.appetit.api.logic.model.Restaurant
import nl.appetit.api.logic.repository.ProductRepository
import nl.appetit.api.logic.repository.RestaurantRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime

class RestaurantProductServiceTest {

    private lateinit var restaurantProductRepository: RestaurantProductR2dbcRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var restaurantRepository: RestaurantRepository
    private lateinit var restaurantProductService: RestaurantProductService

    @BeforeEach
    fun setUp() {
        restaurantProductRepository = mock()
        productRepository = mock()
        restaurantRepository = mock()
        restaurantProductService = RestaurantProductService(
            restaurantProductRepository,
            productRepository,
            restaurantRepository
        )
    }

    @Test
    fun `addProductToRestaurant should create restaurant-product link`() {
        // Arrange
        val restaurantId = 1
        val productId = 5
        val customPrice = BigDecimal("15.50")
        val expectedEntity = RestaurantProductEntity(
            id = 1,
            restaurantId = restaurantId,
            productId = productId,
            customPrice = customPrice,
            isAvailable = true
        )

        whenever(restaurantProductRepository.save(any())).thenReturn(Mono.just(expectedEntity))

        // Act
        val result = restaurantProductService.addProductToRestaurant(
            restaurantId = restaurantId,
            productId = productId,
            customPrice = customPrice,
            isAvailable = true
        )

        // Assert
        StepVerifier.create(result)
            .expectNextMatches {
                it.restaurantId == restaurantId &&
                it.productId == productId &&
                it.customPrice == customPrice &&
                it.isAvailable == true
            }
            .verifyComplete()

        verify(restaurantProductRepository).save(any<RestaurantProductEntity>())
    }

    @Test
    fun `getProductsByRestaurant should return all products for restaurant`() {
        // Arrange
        val restaurantId = 1
        val entities = listOf(
            createRestaurantProductEntity(1, restaurantId, 10),
            createRestaurantProductEntity(2, restaurantId, 11)
        )

        whenever(restaurantProductRepository.findByRestaurantId(restaurantId))
            .thenReturn(Flux.fromIterable(entities))

        // Act
        val result = restaurantProductService.getProductsByRestaurant(restaurantId)

        // Assert
        StepVerifier.create(result)
            .expectNext(entities[0])
            .expectNext(entities[1])
            .verifyComplete()

        verify(restaurantProductRepository).findByRestaurantId(restaurantId)
    }

    @Test
    fun `getAvailableProductsByRestaurant should return only available products`() {
        // Arrange
        val restaurantId = 1
        val availableEntities = listOf(
            createRestaurantProductEntity(1, restaurantId, 10, isAvailable = true)
        )

        whenever(restaurantProductRepository.findByRestaurantIdAndIsAvailable(restaurantId, true))
            .thenReturn(Flux.fromIterable(availableEntities))

        // Act
        val result = restaurantProductService.getAvailableProductsByRestaurant(restaurantId)

        // Assert
        StepVerifier.create(result)
            .expectNext(availableEntities[0])
            .verifyComplete()

        verify(restaurantProductRepository).findByRestaurantIdAndIsAvailable(restaurantId, true)
    }

    @Test
    fun `getProductsByRestaurantWithDetails should return rich product information`() {
        // Arrange
        val restaurantId = 1
        val productId = 10
        val entity = createRestaurantProductEntity(1, restaurantId, productId)
        val product = createProduct(productId, "Pizza Margherita", BigDecimal("12.50"))
        val restaurant = createRestaurant(restaurantId, "Pizzeria Mario")

        whenever(restaurantProductRepository.findByRestaurantId(restaurantId))
            .thenReturn(Flux.just(entity))
        whenever(productRepository.findById(productId))
            .thenReturn(Mono.just(product))
        whenever(restaurantRepository.findById(restaurantId))
            .thenReturn(Mono.just(restaurant))

        // Act
        val result = restaurantProductService.getProductsByRestaurantWithDetails(restaurantId)

        // Assert
        StepVerifier.create(result)
            .expectNextMatches { response ->
                response.productId == productId &&
                response.productName == "Pizza Margherita" &&
                response.basePrice == BigDecimal("12.50") &&
                response.restaurantId == restaurantId &&
                response.restaurantName == "Pizzeria Mario"
            }
            .verifyComplete()

        verify(restaurantProductRepository).findByRestaurantId(restaurantId)
        verify(productRepository).findById(productId)
        verify(restaurantRepository).findById(restaurantId)
    }

    @Test
    fun `linkAllProductsToNewRestaurant should link all existing products to restaurant`() {
        // Arrange
        val restaurantId = 2
        val products = listOf(
            createProduct(10, "Product 1", BigDecimal("10.00")),
            createProduct(11, "Product 2", BigDecimal("15.00"))
        )

        whenever(productRepository.findAll()).thenReturn(Flux.fromIterable(products))
        whenever(restaurantProductRepository.save(any<RestaurantProductEntity>()))
            .thenReturn(Mono.just(createRestaurantProductEntity(1, restaurantId, 10)))

        // Act
        val result = restaurantProductService.linkAllProductsToNewRestaurant(restaurantId)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(productRepository).findAll()
        verify(restaurantProductRepository, times(2)).save(any<RestaurantProductEntity>())
    }

    @Test
    fun `updateProductAvailability should update availability status`() {
        // Arrange
        val restaurantId = 1
        val productId = 10
        val isAvailable = false

        whenever(restaurantProductRepository.updateAvailability(restaurantId, productId, isAvailable))
            .thenReturn(Mono.empty())

        // Act
        val result = restaurantProductService.updateProductAvailability(restaurantId, productId, isAvailable)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(restaurantProductRepository).updateAvailability(restaurantId, productId, isAvailable)
    }

    @Test
    fun `updateProductPrice should update custom price`() {
        // Arrange
        val restaurantId = 1
        val productId = 10
        val customPrice = BigDecimal("20.00")

        whenever(restaurantProductRepository.updateCustomPrice(restaurantId, productId, customPrice))
            .thenReturn(Mono.empty())

        // Act
        val result = restaurantProductService.updateProductPrice(restaurantId, productId, customPrice)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(restaurantProductRepository).updateCustomPrice(restaurantId, productId, customPrice)
    }

    @Test
    fun `isProductAvailableInRestaurant should return true when product is available`() {
        // Arrange
        val restaurantId = 1
        val productId = 10
        val entity = createRestaurantProductEntity(1, restaurantId, productId, isAvailable = true)

        whenever(restaurantProductRepository.findByRestaurantIdAndProductId(restaurantId, productId))
            .thenReturn(Mono.just(entity))

        // Act
        val result = restaurantProductService.isProductAvailableInRestaurant(restaurantId, productId)

        // Assert
        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete()

        verify(restaurantProductRepository).findByRestaurantIdAndProductId(restaurantId, productId)
    }

    @Test
    fun `isProductAvailableInRestaurant should return false when product link does not exist`() {
        // Arrange
        val restaurantId = 1
        val productId = 10

        whenever(restaurantProductRepository.findByRestaurantIdAndProductId(restaurantId, productId))
            .thenReturn(Mono.empty())

        // Act
        val result = restaurantProductService.isProductAvailableInRestaurant(restaurantId, productId)

        // Assert
        StepVerifier.create(result)
            .expectNext(false)
            .verifyComplete()
    }

    @Test
    fun `removeProductFromRestaurant should delete restaurant-product link`() {
        // Arrange
        val restaurantId = 1
        val productId = 10

        whenever(restaurantProductRepository.deleteByRestaurantIdAndProductId(restaurantId, productId))
            .thenReturn(Mono.empty())

        // Act
        val result = restaurantProductService.removeProductFromRestaurant(restaurantId, productId)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(restaurantProductRepository).deleteByRestaurantIdAndProductId(restaurantId, productId)
    }

    // Helper methods
    private fun createRestaurantProductEntity(
        id: Int,
        restaurantId: Int,
        productId: Int,
        customPrice: BigDecimal? = null,
        isAvailable: Boolean = true
    ) = RestaurantProductEntity(
        id = id,
        restaurantId = restaurantId,
        productId = productId,
        customPrice = customPrice,
        isAvailable = isAvailable,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    private fun createProduct(id: Int, name: String, price: BigDecimal) = Product(
        id = id,
        name = name,
        price = price,
        description = "Test product",
        imageUrl = "test.jpg",
        isAvailable = true,
        categoryId = 1,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    private fun createRestaurant(id: Int, name: String) = Restaurant(
        id = id,
        name = name,
        address = "Test Address",
        phone = "123456789",
        email = "test@test.com",
        isActive = true,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
}
