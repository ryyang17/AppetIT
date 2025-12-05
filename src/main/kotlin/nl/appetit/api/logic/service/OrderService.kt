package nl.appetit.api.logic.service

import nl.appetit.api.logic.exception.NotFoundException
import nl.appetit.api.logic.model.Order
import nl.appetit.api.presentation.dto.order.OrderPatch
import nl.appetit.api.presentation.dto.order.OrderResponse
import nl.appetit.api.presentation.mapper.OrderMapper
import nl.appetit.api.logic.repository.OrderRepository
import nl.appetit.api.logic.repository.TableRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderService(
    private val db: OrderRepository,
    private val tableRepository: TableRepository
) {
    fun findAll(): Flux<Order> = db.findAll()

    fun findByTableId(tableId: Int): Flux<Order> = db.findByTableId(tableId)

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
                    }
                }
        } else {
            db.save(order)
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
            .map(OrderMapper::toResponse)
    }
}
