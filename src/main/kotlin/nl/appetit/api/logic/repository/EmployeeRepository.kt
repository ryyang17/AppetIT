package nl.appetit.api.logic.repository

import nl.appetit.api.logic.model.Employee
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface EmployeeRepository {
    fun findAll(): Flux<Employee>
    fun findById(id: Int): Mono<Employee>
    fun save(employee: Employee): Mono<Employee>
    fun deleteById(id: Int): Mono<Void>
    fun deleteAll(): Mono<Void>
}
