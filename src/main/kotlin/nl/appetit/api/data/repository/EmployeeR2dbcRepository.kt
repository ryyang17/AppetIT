package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.EmployeeEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface EmployeeR2dbcRepository : ReactiveCrudRepository<EmployeeEntity, Int> {
    // Find all active employees
    fun findByActiveTrue(): Flux<EmployeeEntity>

    // Find employee by personnel number
    fun findByPersonnelNumber(personnelNumber: String): Mono<EmployeeEntity>

    // Find employees by role
    fun findByRole(role: String): Flux<EmployeeEntity>

    // Find employees by restaurant
    fun findByRestaurantId(restaurantId: Int): Flux<EmployeeEntity>

    // Find active employees by restaurant
    fun findByRestaurantIdAndActiveTrue(restaurantId: Int): Flux<EmployeeEntity>
}