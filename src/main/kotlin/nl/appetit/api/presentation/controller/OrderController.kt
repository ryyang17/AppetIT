package nl.appetit.api.presentation.controller

import nl.appetit.api.data.entity.OrderEntity
import nl.appetit.api.logic.model.Order
import nl.appetit.api.logic.service.OrderService
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

fun OrderRequest.toModel() = Order(
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
    private val orderService: OrderService
) {

    @GetMapping
    fun list(): Flux<Order> = orderService.findAll()

    @PostMapping
    fun insert(@RequestBody request: Mono<OrderRequest>): Mono<Order> =
        request.flatMap { orderService.save(it.toModel()) }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): Mono<Void> = orderService.deleteById(id)

    @DeleteMapping
    fun deleteAll(): Mono<Void> = orderService.deleteAll()
}