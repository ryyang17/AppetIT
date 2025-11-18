package nl.appetit.api.presentation.controller

import nl.appetit.api.data.entity.OrderEntity
import nl.appetit.api.logic.model.Order
import nl.appetit.api.logic.service.OrderService
import nl.appetit.api.presentation.dto.order.OrderRequest
import nl.appetit.api.presentation.dto.order.OrderResponse
import nl.appetit.api.presentation.mapper.OrderMapper
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Instant

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService
) {

    @GetMapping
    fun list(): Flux<OrderResponse> = orderService.findAll()
        .map(OrderMapper::toResponse)

    @GetMapping("/table/{tableId}")
    fun listByTable(@PathVariable tableId: Int): Flux<OrderResponse> =
        orderService.findByTableId(tableId)
            .map(OrderMapper::toResponse)

    @PostMapping
    fun insert(@RequestBody request: Mono<OrderRequest>): Mono<OrderResponse> =
        request.flatMap { order ->
            orderService.save(OrderMapper.toModel(order))
                .map(OrderMapper::toResponse)
        }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): Mono<Void> = orderService.deleteById(id)

    @DeleteMapping
    fun deleteAll(): Mono<Void> = orderService.deleteAll()
}