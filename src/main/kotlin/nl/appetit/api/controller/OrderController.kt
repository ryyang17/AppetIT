package nl.appetit.api.controller

import nl.appetit.api.model.Order
import nl.appetit.api.service.OrderService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Instant

data class OrderRequest(
    val tableId: Int?,
    val restaurantId: Int?,
    val staffId: Int?,
    val status: String?,
    val claimedByStaffId: Int?,
    val preparedByStaffId: Int?,
    val totalAmount: BigDecimal?
)

fun OrderRequest.toEntity() = Order(
    tableId = this.tableId,
    restaurantId = this.restaurantId,
    staffId = this.staffId,
    status = this.status ?: "PENDING",
    claimedByStaffId = this.claimedByStaffId,
    preparedByStaffId = this.preparedByStaffId,
    totalAmount = this.totalAmount,
    createdAt = Instant.now(),
    updatedAt = Instant.now()
)

@RestController
@RequestMapping("/orders")
class OrderController(
    private val service: OrderService
) {

    @GetMapping
    fun list(): Flux<Order> = service.getAllOrders()

    @PostMapping
    fun insert(@RequestBody request: Mono<OrderRequest>): Mono<Order> =
        request.flatMap { service.insertOrder(it.toEntity()) }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): Mono<Void> = service.deleteOrderById(id)

    @DeleteMapping
    fun deleteAll(): Mono<Void> = service.deleteAllOrders()
}