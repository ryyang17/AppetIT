package nl.appetit.api.presentation.controller

import nl.appetit.api.logic.model.Employee
import nl.appetit.api.logic.service.EmployeeService
import nl.appetit.api.presentation.EmployeeRequest
import nl.appetit.api.presentation.toModel
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/employees")
class EmployeeController(
    private val service: EmployeeService
) {

    @GetMapping
    fun list(): Flux<Employee> = service.findAll()

    @GetMapping("/active")
    fun listActive(): Flux<Employee> = service.findByActiveTrue()

    @PostMapping
    fun insert(@RequestBody request: EmployeeRequest): Mono<Employee> =
        service.save(request.toModel())

    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody request: EmployeeRequest): Mono<Employee> =
        service.update(id, request.toModel())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): Mono<Void> =
        service.deleteById(id)

    @DeleteMapping
    fun deleteAll(): Mono<Void> =
        service.deleteAll()
}
