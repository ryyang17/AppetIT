package nl.appetit.api.presentation.controller

import nl.appetit.api.logic.model.OrderItem
import nl.appetit.api.logic.service.OrderItemService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

data class OrderItemRequest(
    val orderId: Int,
    val productId: Int?,
    val staffId: Int?,
    val quantity: Int,
    val status: String?
)

fun OrderItemRequest.toEntity() = OrderItem(
    orderId = orderId,
    productId = productId,
    staffId = staffId,
    quantity = quantity,
    status = status ?: "PENDING"
)

@RestController
@RequestMapping("/order-items")
class OrderItemController(
    private val service: OrderItemService
) {

    @GetMapping
    fun list(): Flux<OrderItem> = service.getAllOrderItems()

    @GetMapping("/order/{orderId}")
    fun listByOrder(@PathVariable orderId: Int): Flux<OrderItem> =
        service.getItemsByOrder(orderId)

    @PostMapping
    fun insert(@RequestBody request: Mono<OrderItemRequest>): Mono<OrderItem> =
        request.flatMap { service.insertOrderItem(it.toEntity()) }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): Mono<Void> = service.deleteOrderItemById(id)

    @DeleteMapping
    fun deleteAll(): Mono<Void> = service.deleteAllOrderItems()
}