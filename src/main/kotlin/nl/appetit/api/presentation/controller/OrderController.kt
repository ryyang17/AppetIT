package nl.appetit.api.presentation.controller

import nl.appetit.api.presentation.dto.order.OrderPatch
import nl.appetit.api.logic.service.OrderService
import nl.appetit.api.presentation.dto.order.OrderRequest
import nl.appetit.api.presentation.dto.order.OrderResponse
import nl.appetit.api.presentation.mapper.OrderMapper
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService
) {
    private val logger = LoggerFactory.getLogger(OrderController::class.java)

    @GetMapping
    fun list(@RequestParam(required = false) status: String?): Flux<OrderResponse> {
        logger.info("GET /orders called with status parameter: {}", status)
        return if (status != null) {
            logger.info("Filtering orders by status: {}", status)
            orderService.findByStatusWithTableData(status)
                .doOnNext { order ->
                    logger.debug("Found order with status {}: id={}, tableId={}", status, order.id, order.tableId)
                }
                .doOnComplete {
                    logger.info("Completed fetching orders with status: {}", status)
                }
                .doOnError { error ->
                    logger.error("Error fetching orders with status {}: {}", status, error.message, error)
                }
        } else {
            logger.info("Fetching all orders (no status filter)")
            orderService.findAllWithTableData()
                .doOnComplete {
                    logger.info("Completed fetching all orders")
                }
                .doOnError { error ->
                    logger.error("Error fetching all orders: {}", error.message, error)
                }
        }
    }

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

    @PostMapping("/table/{tableId}/complete")
    fun completeOrdersForTable(@PathVariable tableId: Int): Mono<Map<String, Any>> {
        logger.info("POST /orders/table/{}/complete called", tableId)
        return orderService.completeOrdersForTable(tableId)
            .doOnSuccess { result -> 
                logger.info("Successfully completed {} orders for table {}", result["completedCount"], tableId)
            }
            .doOnError { e -> 
                logger.error("Failed to complete orders for table {}: {}", tableId, e.message, e)
            }
    }
}