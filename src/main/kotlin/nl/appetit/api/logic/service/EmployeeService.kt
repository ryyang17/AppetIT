package nl.appetit.api.logic.service

import nl.appetit.api.logic.model.Employee
import nl.appetit.api.logic.model.EmployeeRole
import nl.appetit.api.logic.repository.EmployeeRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class EmployeeService(
    private val db: EmployeeRepository
) {
    fun findAll(): Flux<Employee> =
        db.findAll()

    fun findById(id: Int): Mono<Employee> =
        db.findById(id)

    fun save(employee: Employee): Mono<Employee> =
        db.save(employee)

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
                db.save(updated)
            }

    // Get all active employees
    fun findByActiveTrue(): Flux<Employee> =
        db.findByActiveTrue()

    // Get employee by personnel number
    fun findByPersonnelNumber(personnelNumber: String): Mono<Employee> =
        db.findByPersonnelNumber(personnelNumber)

    // Get employees by role
    fun findByRole(role: EmployeeRole): Flux<Employee> =
        db.findByRole(role)

    // Get employees by restaurant
    fun findByRestaurantId(restaurantId: Int): Flux<Employee> =
        db.findByRestaurantId(restaurantId)

    // Get active employees by restaurant
    fun findByRestaurantIdAndActiveTrue(restaurantId: Int): Flux<Employee> =
        db.findByRestaurantIdAndActiveTrue(restaurantId)

    // Business operations for employee management
    fun deactivateEmployee(id: Int): Mono<Employee> =
        db.findById(id)
            .switchIfEmpty(Mono.error(RuntimeException("Employee not found with id: $id")))
            .flatMap { employee ->
                val deactivated = employee.copy(active = false)
                db.save(deactivated)
            }

    fun activateEmployee(id: Int): Mono<Employee> =
        db.findById(id)
            .switchIfEmpty(Mono.error(RuntimeException("Employee not found with id: $id")))
            .flatMap { employee ->
                val activated = employee.copy(active = true)
                db.save(activated)
            }
}
