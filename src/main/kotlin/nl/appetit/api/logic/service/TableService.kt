package nl.appetit.api.logic.service

import nl.appetit.api.logic.model.Table
import nl.appetit.api.logic.repository.TableRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TableService(
    private val tableRepository: TableRepository
) {
    fun findAll(): Flux<Table> =
        tableRepository.findAll()

    fun findById(id: Int): Mono<Table> =
        tableRepository.findById(id)

    fun save(table: Table): Mono<Table> =
        tableRepository.save(table)

    fun deleteById(id: Int): Mono<Void> =
        tableRepository.deleteById(id)

    fun deleteAll(): Mono<Void> =
        tableRepository.deleteAll()

    fun update(id: Int, newTable: Table): Mono<Table> =
        tableRepository.findById(id)
            .switchIfEmpty(Mono.error(RuntimeException("Table not found with id: $id")))
            .flatMap { existing ->
                val updated = existing.copy(
                    restaurantId = newTable.restaurantId,
                    tableNumber = newTable.tableNumber,
                    capacity = newTable.capacity,
                    isActive = newTable.isActive
                )
                tableRepository.save(updated)
            }

    fun findAllByRestaurantId(restaurantId: Int): Flux<Table> =
        tableRepository.findAllByRestaurantId(restaurantId)
}