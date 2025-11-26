package nl.appetit.api.logic.service

import nl.appetit.api.logic.model.Table
import nl.appetit.api.logic.repository.TableRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class TableServiceTest {

    private lateinit var tableRepository: TableRepository
    private lateinit var tableService: TableService

    @BeforeEach
    fun setUp() {
        tableRepository = mock()
        tableService = TableService(tableRepository)
    }

    @Test
    fun `findAll should return all tables`() {
        // Arrange
        val tables = listOf(
            createTable(1, 1, 1, 4),
            createTable(2, 1, 2, 6),
            createTable(3, 2, 1, 2)
        )
        whenever(tableRepository.findAll()).thenReturn(Flux.fromIterable(tables))

        // Act
        val result = tableService.findAll()

        // Assert
        StepVerifier.create(result)
            .expectNext(tables[0])
            .expectNext(tables[1])
            .expectNext(tables[2])
            .verifyComplete()

        verify(tableRepository, times(1)).findAll()
    }

    @Test
    fun `findById should return table when found`() {
        // Arrange
        val tableId = 1
        val table = createTable(tableId, 1, 5, 4)
        whenever(tableRepository.findById(tableId)).thenReturn(Mono.just(table))

        // Act
        val result = tableService.findById(tableId)

        // Assert
        StepVerifier.create(result)
            .expectNext(table)
            .verifyComplete()

        verify(tableRepository, times(1)).findById(tableId)
    }

    @Test
    fun `findById should return empty when table not found`() {
        // Arrange
        val tableId = 999
        whenever(tableRepository.findById(tableId)).thenReturn(Mono.empty())

        // Act
        val result = tableService.findById(tableId)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(tableRepository, times(1)).findById(tableId)
    }

    @Test
    fun `save should return saved table`() {
        // Arrange
        val newTable = createTable(null, 1, 3, 8)
        val savedTable = createTable(1, 1, 3, 8)
        whenever(tableRepository.save(any())).thenReturn(Mono.just(savedTable))

        // Act
        val result = tableService.save(newTable)

        // Assert
        StepVerifier.create(result)
            .expectNext(savedTable)
            .verifyComplete()

        verify(tableRepository, times(1)).save(newTable)
    }

    @Test
    fun `deleteById should delete table`() {
        // Arrange
        val tableId = 1
        whenever(tableRepository.deleteById(tableId)).thenReturn(Mono.empty())

        // Act
        val result = tableService.deleteById(tableId)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(tableRepository, times(1)).deleteById(tableId)
    }

    @Test
    fun `deleteAll should delete all tables`() {
        // Arrange
        whenever(tableRepository.deleteAll()).thenReturn(Mono.empty())

        // Act
        val result = tableService.deleteAll()

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(tableRepository, times(1)).deleteAll()
    }

    @Test
    fun `update should update existing table`() {
        // Arrange
        val tableId = 1
        val existingTable = createTable(tableId, 1, 5, 4, true)
        val updatedTableData = createTable(tableId, 1, 5, 6, false)
        val expectedUpdatedTable = existingTable.copy(
            restaurantId = updatedTableData.restaurantId,
            tableNumber = updatedTableData.tableNumber,
            capacity = updatedTableData.capacity,
            isActive = updatedTableData.isActive
        )

        whenever(tableRepository.findById(tableId)).thenReturn(Mono.just(existingTable))
        whenever(tableRepository.save(any())).thenReturn(Mono.just(expectedUpdatedTable))

        // Act
        val result = tableService.update(tableId, updatedTableData)

        // Assert
        StepVerifier.create(result)
            .expectNext(expectedUpdatedTable)
            .verifyComplete()

        verify(tableRepository, times(1)).findById(tableId)
        verify(tableRepository, times(1)).save(any())
    }

    @Test
    fun `update should throw error when table not found`() {
        // Arrange
        val tableId = 999
        val tableToUpdate = createTable(tableId, 1, 5, 6)
        whenever(tableRepository.findById(tableId)).thenReturn(Mono.empty())

        // Act
        val result = tableService.update(tableId, tableToUpdate)

        // Assert
        StepVerifier.create(result)
            .expectError(RuntimeException::class.java)
            .verify()

        verify(tableRepository, times(1)).findById(tableId)
        verify(tableRepository, never()).save(any())
    }

    @Test
    fun `findAllByRestaurantId should return tables for specific restaurant`() {
        // Arrange
        val restaurantId = 1
        val tablesForRestaurant = listOf(
            createTable(1, restaurantId, 1, 4),
            createTable(2, restaurantId, 2, 6),
            createTable(3, restaurantId, 3, 2)
        )
        whenever(tableRepository.findAllByRestaurantId(restaurantId))
            .thenReturn(Flux.fromIterable(tablesForRestaurant))

        // Act
        val result = tableService.findAllByRestaurantId(restaurantId)

        // Assert
        StepVerifier.create(result)
            .expectNext(tablesForRestaurant[0])
            .expectNext(tablesForRestaurant[1])
            .expectNext(tablesForRestaurant[2])
            .verifyComplete()

        verify(tableRepository, times(1)).findAllByRestaurantId(restaurantId)
    }

    @Test
    fun `findAllByRestaurantId should return empty when no tables found for restaurant`() {
        // Arrange
        val restaurantId = 999
        whenever(tableRepository.findAllByRestaurantId(restaurantId))
            .thenReturn(Flux.empty())

        // Act
        val result = tableService.findAllByRestaurantId(restaurantId)

        // Assert
        StepVerifier.create(result)
            .verifyComplete()

        verify(tableRepository, times(1)).findAllByRestaurantId(restaurantId)
    }

    @Test
    fun `update should preserve existing values when updating with new data`() {
        // Arrange
        val tableId = 1
        val existingTable = createTable(tableId, 1, 5, 4, true)
        val updateData = createTable(tableId, 2, 10, 8, false)
        val expectedTable = createTable(tableId, 2, 10, 8, false)

        whenever(tableRepository.findById(tableId)).thenReturn(Mono.just(existingTable))
        whenever(tableRepository.save(any())).thenAnswer { invocation ->
            val savedTable = invocation.getArgument<Table>(0)
            Mono.just(savedTable)
        }

        // Act
        val result = tableService.update(tableId, updateData)

        // Assert
        StepVerifier.create(result)
            .expectNext(expectedTable)
            .verifyComplete()

        verify(tableRepository, times(1)).findById(tableId)
        verify(tableRepository, times(1)).save(any())
    }

    // Helper function to create test Table objects
    private fun createTable(
        id: Int?,
        restaurantId: Int,
        tableNumber: Int,
        capacity: Int?,
        isActive: Boolean = true
    ): Table {
        return Table(
            id = id,
            restaurantId = restaurantId,
            tableNumber = tableNumber,
            capacity = capacity,
            isActive = isActive
        )
    }
}