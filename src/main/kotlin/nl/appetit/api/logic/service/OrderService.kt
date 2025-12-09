package nl.appetit.api.logic.service

import nl.appetit.api.logic.exception.NotFoundException
import nl.appetit.api.logic.model.Order
import nl.appetit.api.presentation.dto.order.OrderPatch
import nl.appetit.api.presentation.dto.order.OrderResponse
import nl.appetit.api.presentation.mapper.OrderMapper
import nl.appetit.api.presentation.mapper.TableMapper
import nl.appetit.api.logic.repository.OrderRepository
import nl.appetit.api.logic.repository.TableRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderService(
    private val db: OrderRepository,
    private val tableRepository: TableRepository,
    private val barUpdateService: BarUpdateService
) {
    fun findAll(): Flux<Order> = db.findAll()

    fun findByTableId(tableId: Int): Flux<Order> = db.findByTableId(tableId)

    /**
     * Get all orders with table data included to avoid N+1 queries
     */
    fun findAllWithTableData(): Flux<OrderResponse> =
        db.findAll().flatMap { order ->
            enrichOrderWithTableData(order)
        }

    /**
     * Get orders by table ID with table data included
     */
    fun findByTableIdWithTableData(tableId: Int): Flux<OrderResponse> =
        db.findByTableId(tableId).flatMap { order ->
            enrichOrderWithTableData(order)
        }

    /**
     * Helper method to enrich an order with its table data
     */
    private fun enrichOrderWithTableData(order: Order): Mono<OrderResponse> {
        return if (order.tableId != null) {
            tableRepository.findById(order.tableId)
                .map { table ->
                    OrderMapper.toResponse(order, TableMapper.toResponse(table))
                }
                .switchIfEmpty(Mono.just(OrderMapper.toResponse(order, null)))
        } else {
            Mono.just(OrderMapper.toResponse(order, null))
        }
    }

    fun save(order: Order): Mono<Order> {
        val tableId = order.tableId
        return if (tableId != null) {
            tableRepository.findById(tableId)
                .switchIfEmpty(Mono.error(NotFoundException("Table with id $tableId not found")))
                .flatMap { table ->
                    if (!table.isActive) {
                        Mono.error<Order>(NotFoundException("Table with id $tableId is not active"))
                    } else if (order.restaurantId != null && order.restaurantId != table.restaurantId) {
                        Mono.error<Order>(IllegalArgumentException("Table $tableId does not belong to restaurant ${order.restaurantId}"))
                    } else {
                        db.save(order)
                            .doOnNext { savedOrder ->
                                barUpdateService.pushUpdate("${order.tableId}", "${savedOrder.id}")
                            }
                    }
                }
        } else {
            db.save(order)
                .doOnNext { savedOrder ->
                    barUpdateService.pushUpdate("${order.tableId}", "${savedOrder.id}")
                }
        }
    }

    fun deleteById(id: Int): Mono<Void> = db.deleteById(id)

    fun deleteAll(): Mono<Void> = db.deleteAll()

    fun patchOrder(id: Int, patch: OrderPatch): Mono<OrderResponse> {
        return db.findById(id)
            .switchIfEmpty(Mono.error(NotFoundException("Order with id $id not found")))
            .flatMap { existing: Order ->
                val updated = existing.copy(
                    status = patch.status ?: existing.status,
                    claimedByStaffId = patch.claimedByStaffId ?: existing.claimedByStaffId,
                    preparedByStaffId = patch.preparedByStaffId ?: existing.preparedByStaffId
                )

                db.save(updated)
            }
            .flatMap { savedOrder ->
                enrichOrderWithTableData(savedOrder)
            }
    }
}
