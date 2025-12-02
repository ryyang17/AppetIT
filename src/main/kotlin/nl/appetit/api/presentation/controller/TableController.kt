package nl.appetit.api.presentation.controller

import jakarta.validation.Valid
import nl.appetit.api.logic.service.TableService
import nl.appetit.api.presentation.dto.table.TableRequest
import nl.appetit.api.presentation.dto.table.TableResponse
import nl.appetit.api.presentation.mapper.TableMapper
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/tables")
class TableController(
    private val tableService: TableService
) {

    @GetMapping
    fun list(): Flux<TableResponse> {
        return tableService.findAll()
            .map(TableMapper::toResponse)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Int): Mono<TableResponse> {
        return tableService.findById(id)
            .map(TableMapper::toResponse)
    }

    @GetMapping("/restaurant/{restaurantId}")
    fun listByRestaurant(@PathVariable restaurantId: Int): Flux<TableResponse> {
        return tableService.findAllByRestaurantId(restaurantId)
            .map(TableMapper::toResponse)
    }

    @PostMapping
    fun insert(@Valid @RequestBody request: Mono<TableRequest>): Mono<TableResponse> {
        return request.flatMap { tableRequest ->
            tableService.save(TableMapper.toModel(tableRequest))
                .map(TableMapper::toResponse)
        }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @Valid @RequestBody request: Mono<TableRequest>): Mono<TableResponse> {
        return request.flatMap { tableRequest ->
            tableService.update(id, TableMapper.toModel(tableRequest))
                .map(TableMapper::toResponse)
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): Mono<Void> {
        return tableService.deleteById(id)
    }

}