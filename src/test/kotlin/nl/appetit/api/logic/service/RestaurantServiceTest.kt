package nl.appetit.api.logic.service

import nl.appetit.api.logic.exception.NotFoundException
import nl.appetit.api.logic.model.Restaurant
import nl.appetit.api.logic.model.RestaurantUpdate
import nl.appetit.api.logic.repository.RestaurantRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class RestaurantServiceTest {
    @Mock
    lateinit var restaurantRepository: RestaurantRepository
    @InjectMocks
    lateinit var restaurantService: RestaurantService
    private lateinit var restaurant: Restaurant

    @BeforeEach
    fun setUp() {
        restaurant = Restaurant(
            name = "test",
            address = "testaddress",
            phone = "1635573466",
            email = "test@test.com",
            isActive = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }

    @Test
    fun `findAll should return restaurants`() {
        whenever(restaurantRepository.findAll()).thenReturn(Flux.just(restaurant))

        StepVerifier.create(restaurantService.findAll())
            .expectNext(restaurant)
            .verifyComplete()
    }

    @Test
    fun `save should persist restaurant`() {
        whenever(restaurantRepository.save(any())).thenReturn(Mono.just(restaurant))

        StepVerifier.create(restaurantService.save(restaurant))
            .expectNext(restaurant)
            .verifyComplete()

        verify(restaurantRepository).save(restaurant)
    }

    @Test
    fun `deleteById should call repository`() {
        whenever(restaurantRepository.deleteById(1)).thenReturn(Mono.empty())

        StepVerifier.create(restaurantService.deleteById(1))
            .verifyComplete()

        verify(restaurantRepository).deleteById(1)
    }

    @Test
    fun `update should update and return restaurant when exists`() {
        val update = RestaurantUpdate(name = "New Name")

        val updatedRestaurant = restaurant.copy(name = "New Name")

        whenever(restaurantRepository.findById(1)).thenReturn(Mono.just(restaurant))
        whenever(restaurantRepository.save(any())).thenReturn(Mono.just(updatedRestaurant))

        StepVerifier.create(restaurantService.update(1, update))
            .expectNextMatches { it.name == "New Name" && it.address == restaurant.address }
            .verifyComplete()

        verify(restaurantRepository).findById(1)
        verify(restaurantRepository).save(any())
    }

    @Test
    fun `update should return error when restaurant not found`() {
        whenever(restaurantRepository.findById(1)).thenReturn(Mono.empty())

        StepVerifier.create(restaurantService.update(1, RestaurantUpdate(name = "X")))
            .expectErrorMatches { it is NotFoundException && it.message == "Restaurant with id 1 not found" }
            .verify()
    }

    @Test
    fun `update should not overwrite fields when update dto contains nulls`() {
        val update = RestaurantUpdate(name = null, phone = "9999")

        val saved = restaurant.copy(phone = "9999")

        whenever(restaurantRepository.findById(1)).thenReturn(Mono.just(restaurant))
        whenever(restaurantRepository.save(any())).thenReturn(Mono.just(saved))

        StepVerifier.create(restaurantService.update(1, update))
            .expectNextMatches {
                it.name == restaurant.name && // unchanged
                        it.address == restaurant.address && // unchanged
                        it.phone == "9999" // updated
            }
            .verifyComplete()
    }

    @Test
    fun `update should not call save when restaurant not found`() {
        whenever(restaurantRepository.findById(1)).thenReturn(Mono.empty())

        StepVerifier.create(restaurantService.update(1, RestaurantUpdate()))
            .expectError(NotFoundException::class.java)
            .verify()

        verify(restaurantRepository).findById(1)
        verify(restaurantRepository, Mockito.never()).save(any())
    }

    @Test
    fun `update should update all fields when provided`() {
        val update = RestaurantUpdate(
            name = "A", address = "B", phone = "C", email = "D", isActive = false
        )

        val saved = restaurant.copy(
            name = "A", address = "B", phone = "C", email = "D", isActive = false
        )

        whenever(restaurantRepository.findById(1)).thenReturn(Mono.just(restaurant))
        whenever(restaurantRepository.save(any())).thenReturn(Mono.just(saved))

        StepVerifier.create(restaurantService.update(1, update))
            .expectNext(saved)
            .verifyComplete()
    }

}