package nl.appetit.api.logic.repository

import nl.appetit.api.logic.model.Employee
import nl.appetit.api.logic.model.EmployeeRole
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface EmployeeRepository {
    fun findAll(): Flux<Employee>
    fun findById(id: Int): Mono<Employee>
    fun save(employee: Employee): Mono<Employee>
    fun deleteById(id: Int): Mono<Void>
    fun deleteAll(): Mono<Void>
    fun findByActiveTrue(): Flux<Employee>
    fun findByPersonnelNumber(personnelNumber: String): Mono<Employee>
    fun findByRole(role: EmployeeRole): Flux<Employee>
    fun findByRestaurantId(restaurantId: Int): Flux<Employee>
    fun findByRestaurantIdAndActiveTrue(restaurantId: Int): Flux<Employee>
}
