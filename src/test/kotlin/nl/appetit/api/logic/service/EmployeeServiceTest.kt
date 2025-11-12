package nl.appetit.api.logic.service

import nl.appetit.api.logic.exception.BadRequestException
import nl.appetit.api.logic.exception.NotFoundException
import nl.appetit.api.logic.model.Employee
import nl.appetit.api.logic.model.EmployeeRole
import nl.appetit.api.logic.repository.EmployeeRepository
import nl.appetit.api.logic.repository.RestaurantRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Instant

class EmployeeServiceTest {

    private lateinit var employeeRepository: EmployeeRepository
    private lateinit var restaurantRepository: RestaurantRepository
    private lateinit var employeeService: EmployeeService

    @BeforeEach
    fun setUp() {
        employeeRepository = mock()
        restaurantRepository = mock()
        employeeService = EmployeeService(employeeRepository, restaurantRepository)
    }

    @Test
    fun `findAll should return all employees`() {
        // Arrange
        val employees = listOf(
            createEmployee(1, "John", "Doe"),
            createEmployee(2, "Jane", "Smith")
        )
        whenever(employeeRepository.findAll()).thenReturn(Flux.fromIterable(employees))

        // Act
        val result = employeeService.findAll()

        // Assert
        StepVerifier.create(result)
            .expectNext(employees[0])
            .expectNext(employees[1])
            .verifyComplete()

        verify(employeeRepository, times(1)).findAll()
    }

    @Test
    fun `findById should return employee when found`() {
        // Arrange
        val employeeId = 1
        val employee = createEmployee(employeeId, "John", "Doe")
        whenever(employeeRepository.findById(employeeId)).thenReturn(Mono.just(employee))

        // Act
        val result = employeeService.findById(employeeId)

        // Assert
        StepVerifier.create(result)
            .expectNext(employee)
            .verifyComplete()

        verify(employeeRepository, times(1)).findById(employeeId)
    }

    @Test
    fun `save should save employee without restaurant validation when restaurantId is null`() {
        // Arrange
        val employee = createEmployee(null, "John", "Doe", restaurantId = null)
        val savedEmployee = createEmployee(1, "John", "Doe", restaurantId = null)
        whenever(employeeRepository.save(any())).thenReturn(Mono.just(savedEmployee))

        // Act
        val result = employeeService.save(employee)

        // Assert
        StepVerifier.create(result)
            .expectNext(savedEmployee)
            .verifyComplete()

        verify(employeeRepository, times(1)).save(any())
        verify(restaurantRepository, never()).findById(any())
    }

    @Test
    fun `save should save employee without restaurant validation when restaurantId is zero or negative`() {
        // Arrange
        val employee = createEmployee(null, "John", "Doe", restaurantId = 0)
        val savedEmployee = createEmployee(1, "John", "Doe", restaurantId = 0)
        whenever(employeeRepository.save(any())).thenReturn(Mono.just(savedEmployee))

        // Act
        val result = employeeService.save(employee)

        // Assert
        StepVerifier.create(result)
            .expectNext(savedEmployee)
            .verifyComplete()

        verify(employeeRepository, times(1)).save(any())
        verify(restaurantRepository, never()).findById(any())
    }

    @Test
    fun `save should save employee when restaurant exists`() {
        // Arrange
        val restaurantId = 1
        val employee = createEmployee(null, "John", "Doe", restaurantId = restaurantId)
        val savedEmployee = createEmployee(1, "John", "Doe", restaurantId = restaurantId)
        whenever(restaurantRepository.findById(restaurantId)).thenReturn(Mono.just(mock()))
        whenever(employeeRepository.save(any())).thenReturn(Mono.just(savedEmployee))

        // Act
        val result = employeeService.save(employee)

        // Assert
        StepVerifier.create(result)
            .expectNext(savedEmployee)
            .verifyComplete()

        verify(restaurantRepository, times(1)).findById(restaurantId)
        verify(employeeRepository, times(1)).save(any())
    }

    @Test
    fun `save should throw BadRequestException when restaurant does not exist`() {
        // Arrange
        val restaurantId = 999
        val employee = createEmployee(null, "John", "Doe", restaurantId = restaurantId)
        whenever(restaurantRepository.findById(restaurantId)).thenReturn(Mono.empty())

        // Act
        val result = employeeService.save(employee)

        // Assert
        StepVerifier.create(result)
            .expectError(BadRequestException::class.java)
            .verify()

        verify(restaurantRepository, times(1)).findById(restaurantId)
        verify(employeeRepository, never()).save(any())
    }

    @Test
    fun `update should update existing employee`() {
        // Arrange
        val employeeId = 1
        val existingEmployee = createEmployee(employeeId, "John", "Doe")
        val updatedEmployee = createEmployee(employeeId, "Jane", "Smith")

        whenever(employeeRepository.findById(employeeId)).thenReturn(Mono.just(existingEmployee))
        whenever(employeeRepository.save(any())).thenReturn(Mono.just(updatedEmployee))

        // Act
        val result = employeeService.update(employeeId, updatedEmployee)

        // Assert
        StepVerifier.create(result)
            .expectNextMatches { employee ->
                employee.firstName == "Jane" && employee.lastName == "Smith"
            }
            .verifyComplete()

        verify(employeeRepository, times(1)).findById(employeeId)
        verify(employeeRepository, times(1)).save(any())
    }

    @Test
    fun `update should throw NotFoundException when employee not found`() {
        // Arrange
        val employeeId = 999
        val employeeToUpdate = createEmployee(employeeId, "John", "Doe")
        whenever(employeeRepository.findById(employeeId)).thenReturn(Mono.empty())

        // Act
        val result = employeeService.update(employeeId, employeeToUpdate)

        // Assert
        StepVerifier.create(result)
            .expectError(NotFoundException::class.java)
            .verify()

        verify(employeeRepository, times(1)).findById(employeeId)
        verify(employeeRepository, never()).save(any())
    }

    @Test
    fun `update should validate restaurant when restaurantId is provided`() {
        // Arrange
        val employeeId = 1
        val restaurantId = 2
        val existingEmployee = createEmployee(employeeId, "John", "Doe", restaurantId = null)
        val updatedEmployee = createEmployee(employeeId, "John", "Doe", restaurantId = restaurantId)

        whenever(employeeRepository.findById(employeeId)).thenReturn(Mono.just(existingEmployee))
        whenever(restaurantRepository.findById(restaurantId)).thenReturn(Mono.just(mock()))
        whenever(employeeRepository.save(any())).thenReturn(Mono.just(updatedEmployee))

        // Act
        val result = employeeService.update(employeeId, updatedEmployee)

        // Assert
        StepVerifier.create(result)
            .expectNext(updatedEmployee)
            .verifyComplete()

        verify(employeeRepository, times(1)).findById(employeeId)
        verify(restaurantRepository, times(1)).findById(restaurantId)
        verify(employeeRepository, times(1)).save(any())
    }

    @Test
    fun `update should throw BadRequestException when restaurant does not exist`() {
        // Arrange
        val employeeId = 1
        val restaurantId = 999
        val existingEmployee = createEmployee(employeeId, "John", "Doe", restaurantId = null)
        val updatedEmployee = createEmployee(employeeId, "John", "Doe", restaurantId = restaurantId)

        whenever(employeeRepository.findById(employeeId)).thenReturn(Mono.just(existingEmployee))
        whenever(restaurantRepository.findById(restaurantId)).thenReturn(Mono.empty())

        // Act
        val result = employeeService.update(employeeId, updatedEmployee)

        // Assert
        StepVerifier.create(result)
            .expectError(BadRequestException::class.java)
            .verify()

        verify(employeeRepository, times(1)).findById(employeeId)
        verify(restaurantRepository, times(1)).findById(restaurantId)
        verify(employeeRepository, never()).save(any())
    }

    @Test
    fun `deleteById should delete employee`() {
        // Arrange
        val employeeId = 1
        whenever(employeeRepository.deleteById(employeeId)).thenReturn(Mono.empty())

        // Act
        val result = employeeService.deleteById(employeeId)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(employeeRepository, times(1)).deleteById(employeeId)
    }

    @Test
    fun `deleteAll should delete all employees`() {
        // Arrange
        whenever(employeeRepository.deleteAll()).thenReturn(Mono.empty())

        // Act
        val result = employeeService.deleteAll()

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(employeeRepository, times(1)).deleteAll()
    }

    private fun createEmployee(
        id: Int?,
        firstName: String,
        lastName: String,
        restaurantId: Int? = null,
        role: EmployeeRole = EmployeeRole.WAITER,
        active: Boolean = true
    ): Employee {
        return Employee(
            id = id,
            firstName = firstName,
            lastName = lastName,
            role = role,
            active = active,
            personnelNumber = id,
            restaurantId = restaurantId,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}