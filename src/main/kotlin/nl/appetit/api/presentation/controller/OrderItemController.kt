package nl.appetit.api.presentation.controller

import nl.appetit.api.logic.model.OrderItem
import nl.appetit.api.logic.service.OrderItemService
import nl.appetit.api.presentation.dto.order_item.OrderItemRequest
import nl.appetit.api.presentation.dto.order_item.OrderItemResponse
import nl.appetit.api.presentation.mapper.OrderItemMapper
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/order-items")
class OrderItemController(
    private val service: OrderItemService
) {

    @GetMapping
    fun list(): Flux<OrderItemResponse> = service.getAllOrderItems()
        .map(OrderItemMapper::toResponse)

    @GetMapping("/order/{orderId}")
    fun listByOrder(@PathVariable orderId: Int): Flux<OrderItemResponse> =
        service.getItemsByOrder(orderId)
            .map(OrderItemMapper::toResponse)

    @PostMapping
    fun insert(@RequestBody request: Mono<OrderItemRequest>): Mono<OrderItemResponse> =
        request.flatMap { orderItem ->
            service.insertOrderItem(OrderItemMapper.toModel(orderItem))
                .map(OrderItemMapper::toResponse)
        }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): Mono<Void> = service.deleteOrderItemById(id)

    @DeleteMapping
    fun deleteAll(): Mono<Void> = service.deleteAllOrderItems()
}