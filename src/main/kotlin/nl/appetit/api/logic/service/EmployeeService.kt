package nl.appetit.api.logic.service

import nl.appetit.api.logic.model.Employee
import nl.appetit.api.logic.model.EmployeeRole
import nl.appetit.api.logic.repository.EmployeeRepository
import nl.appetit.api.logic.repository.RestaurantRepository
import nl.appetit.api.logic.exception.BadRequestException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Suppress("unused")
@Service
class EmployeeService(
    private val db: EmployeeRepository,
    private val restaurantRepository: RestaurantRepository
) {
    fun findAll(): Flux<Employee> =
        db.findAll()

    @Suppress("unused")
    fun findById(id: Int): Mono<Employee> =
        db.findById(id)

    fun save(employee: Employee): Mono<Employee> =
        if (employee.restaurantId == null || employee.restaurantId <= 0) {
            db.save(employee)
        } else {
            restaurantRepository.existsById(employee.restaurantId)
                .flatMap { exists ->
                    if (exists == true) db.save(employee)
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
                    restaurantId = newEmployee.restaurantId
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

    // Get all active employees
    fun findByActiveTrue(): Flux<Employee> =
        db.findByActiveTrue()

    // Get employee by personnel number
    @Suppress("unused")
    fun findByPersonnelNumber(personnelNumber: String): Mono<Employee> =
        db.findByPersonnelNumber(personnelNumber)

    // Get employees by role
    @Suppress("unused")
    fun findByRole(role: EmployeeRole): Flux<Employee> =
        db.findByRole(role)

    // Get employees by restaurant
    @Suppress("unused")
    fun findByRestaurantId(restaurantId: Int): Flux<Employee> =
        db.findByRestaurantId(restaurantId)

    // Get active employees by restaurant
    @Suppress("unused")
    fun findByRestaurantIdAndActiveTrue(restaurantId: Int): Flux<Employee> =
        db.findByRestaurantIdAndActiveTrue(restaurantId)

    // Business operations for employee management
    @Suppress("unused")
    fun deactivateEmployee(id: Int): Mono<Employee> =
        db.findById(id)
            .switchIfEmpty(Mono.error(RuntimeException("Employee not found with id: $id")))
            .flatMap { employee ->
                val deactivated = employee.copy(active = false)
                db.save(deactivated)
            }

    @Suppress("unused")
    fun activateEmployee(id: Int): Mono<Employee> =
        db.findById(id)
            .switchIfEmpty(Mono.error(RuntimeException("Employee not found with id: $id")))
            .flatMap { employee ->
                val activated = employee.copy(active = true)
                db.save(activated)
            }
}
