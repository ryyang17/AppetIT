package nl.appetit.api.logic.service

import nl.appetit.api.logic.exception.BadRequestException
import nl.appetit.api.logic.exception.NotFoundException
import nl.appetit.api.logic.model.Employee
import nl.appetit.api.logic.repository.EmployeeRepository
import nl.appetit.api.logic.repository.RestaurantRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class EmployeeService(
    private val db: EmployeeRepository,
    private val restaurantRepository: RestaurantRepository
) {
    fun findAll(): Flux<Employee> = db.findAll()

    fun findById(id: Int): Mono<Employee> = db.findById(id)

    fun save(employee: Employee): Mono<Employee> {
        val withTimestamps = employee.copy(
            createdAt = employee.createdAt ?: Instant.now(),
            updatedAt = Instant.now()
        )
        return saveWithRestaurantValidation(withTimestamps)
    }

    fun update(id: Int, employee: Employee): Mono<Employee> =
        db.findById(id)
            .switchIfEmpty(Mono.error(NotFoundException("Employee with id $id not found")))
            .flatMap { existing ->
                val updated = existing.copy(
                    firstName = employee.firstName,
                    lastName = employee.lastName,
                    role = employee.role,
                    active = employee.active,
                    personnelNumber = employee.personnelNumber,
                    restaurantId = employee.restaurantId,
                    updatedAt = Instant.now()
                )
                saveWithRestaurantValidation(updated)
            }

    fun deleteById(id: Int): Mono<Void> = db.deleteById(id)

    fun deleteAll(): Mono<Void> = db.deleteAll()

    private fun saveWithRestaurantValidation(employee: Employee): Mono<Employee> =
        if (employee.restaurantId == null || employee.restaurantId <= 0) {
            db.save(employee)
        } else {
            restaurantRepository.findById(employee.restaurantId)
                .hasElement()
                .flatMap { exists ->
                    if (exists) {
                        db.save(employee)
                    } else {
                        Mono.error(BadRequestException("Restaurant with id ${employee.restaurantId} does not exist"))
                    }
                }
        }
}
