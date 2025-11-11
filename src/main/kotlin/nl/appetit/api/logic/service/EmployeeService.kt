package nl.appetit.api.logic.service

import nl.appetit.api.logic.model.Employee
import nl.appetit.api.logic.repository.EmployeeRepository
import nl.appetit.api.logic.repository.RestaurantRepository
import nl.appetit.api.logic.exception.BadRequestException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class EmployeeService(
    private val db: EmployeeRepository,
    private val restaurantRepository: RestaurantRepository
) {
    fun findAll(): Flux<Employee> =
        db.findAll()

    fun findById(id: Int): Mono<Employee> =
        db.findById(id)

    fun save(employee: Employee): Mono<Employee> =
        if (employee.restaurantId == null || employee.restaurantId <= 0) {
            val now = Instant.now()
            val withTimestamps = employee.copy(
                createdAt = employee.createdAt ?: now,
                updatedAt = now
            )
            db.save(withTimestamps)
        } else {
            restaurantRepository.existsById(employee.restaurantId)
                .flatMap { exists ->
                    val now = Instant.now()
                    val withTimestamps = employee.copy(
                        createdAt = employee.createdAt ?: now,
                        updatedAt = now
                    )
                    if (exists == true) db.save(withTimestamps)
                    else Mono.error(BadRequestException("Restaurant with id ${employee.restaurantId} does not exist"))
                }
        }

    fun deleteById(id: Int): Mono<Void> =
        db.deleteById(id)

    fun deleteAll(): Mono<Void> =
        db.deleteAll()

    fun update(id: Int, newEmployee: Employee): Mono<Employee> =
        db.findById(id)
            .switchIfEmpty(Mono.error(RuntimeException("Employee not found with id: $id")))
            .flatMap { existing ->
                val updated = existing.copy(
                    firstName = newEmployee.firstName,
                    lastName = newEmployee.lastName,
                    role = newEmployee.role,
                    active = newEmployee.active,
                    personnelNumber = newEmployee.personnelNumber,
                    restaurantId = newEmployee.restaurantId,
                    createdAt = existing.createdAt,
                    updatedAt = Instant.now()
                )
                if (updated.restaurantId == null) {
                    db.save(updated)
                } else {
                    restaurantRepository.existsById(updated.restaurantId)
                        .flatMap { exists ->
                            if (exists) db.save(updated)
                            else Mono.error(BadRequestException("Restaurant with id ${updated.restaurantId} does not exist"))
                        }
                }
            }
}
