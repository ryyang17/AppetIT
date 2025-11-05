package nl.appetit.api.presentation.controller

import nl.appetit.api.logic.service.EmployeeService
import nl.appetit.api.presentation.dto.employee.EmployeeRequest
import nl.appetit.api.presentation.dto.employee.EmployeeResponse
import nl.appetit.api.presentation.mapper.EmployeeMapper.toModel
import nl.appetit.api.presentation.mapper.EmployeeMapper.toResponse
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/employees")
class EmployeeController(
    private val service: EmployeeService
) {

    @GetMapping
    fun list(): Flux<EmployeeResponse> = service.findAll().map { it.toResponse() }

    @GetMapping("/active")
    fun listActive(): Flux<EmployeeResponse> = service.findByActiveTrue().map { it.toResponse() }

    @PostMapping
    fun insert(@RequestBody request: EmployeeRequest): Mono<EmployeeResponse> =
        service.save(request.toModel()).map { it.toResponse() }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody request: EmployeeRequest): Mono<EmployeeResponse> =
        service.update(id, request.toModel()).map { it.toResponse() }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): Mono<Void> =
        service.deleteById(id)

    @DeleteMapping
    fun deleteAll(): Mono<Void> =
        service.deleteAll()
}
