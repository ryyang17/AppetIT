package nl.appetit.api.presentation.controller

import nl.appetit.api.presentation.dto.order.OrderPatch
import nl.appetit.api.logic.service.OrderService
import nl.appetit.api.presentation.dto.order.OrderRequest
import nl.appetit.api.presentation.dto.order.OrderResponse
import nl.appetit.api.presentation.mapper.OrderMapper
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService
) {

    @GetMapping
    fun list(): Flux<OrderResponse> = orderService.findAllWithTableData()

    @GetMapping("/table/{tableId}")
    fun listByTable(@PathVariable tableId: Int): Flux<OrderResponse> =
        orderService.findByTableIdWithTableData(tableId)

    @PostMapping
    fun insert(@RequestBody request: Mono<OrderRequest>): Mono<OrderResponse> =
        request.flatMap { order ->
            orderService.save(OrderMapper.toModel(order))
                .map { savedOrder -> OrderMapper.toResponse(savedOrder) }
        }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): Mono<Void> = orderService.deleteById(id)

    @DeleteMapping
    fun deleteAll(): Mono<Void> = orderService.deleteAll()

    @PatchMapping("/{id}")
    fun patchOrder(
        @PathVariable id: Int,
        @RequestBody patch: OrderPatch
    ): Mono<OrderResponse> =
        orderService.patchOrder(id, patch)
}