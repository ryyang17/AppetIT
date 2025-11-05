package nl.appetit.api.data.repository.impl

import nl.appetit.api.data.mapper.EmployeeMapper
import nl.appetit.api.data.repository.EmployeeR2dbcRepository
import nl.appetit.api.logic.model.Employee
import nl.appetit.api.logic.model.EmployeeRole
import nl.appetit.api.logic.repository.EmployeeRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class EmployeeRepositoryImpl(
    private val db: EmployeeR2dbcRepository
) : EmployeeRepository {

    override fun findAll(): Flux<Employee> =
        db.findAll()
            .map(EmployeeMapper::toModel)

    override fun findById(id: Int): Mono<Employee> =
        db.findById(id)
            .map(EmployeeMapper::toModel)

    override fun save(employee: Employee): Mono<Employee> =
        db.save(EmployeeMapper.toEntity(employee))
            .map(EmployeeMapper::toModel)

    override fun deleteById(id: Int): Mono<Void> =
        db.deleteById(id)

    override fun deleteAll(): Mono<Void> =
        db.deleteAll()

    override fun findByActiveTrue(): Flux<Employee> =
        db.findByActiveTrue()
            .map(EmployeeMapper::toModel)

    override fun findByPersonnelNumber(personnelNumber: String): Mono<Employee> =
        db.findByPersonnelNumber(personnelNumber)
            .map(EmployeeMapper::toModel)

    override fun findByRole(role: EmployeeRole): Flux<Employee> =
        db.findByRole(role.name)
            .map(EmployeeMapper::toModel)

    override fun findByRestaurantId(restaurantId: Int): Flux<Employee> =
        db.findByRestaurantId(restaurantId)
            .map(EmployeeMapper::toModel)

    override fun findByRestaurantIdAndActiveTrue(restaurantId: Int): Flux<Employee> =
        db.findByRestaurantIdAndActiveTrue(restaurantId)
            .map(EmployeeMapper::toModel)
}