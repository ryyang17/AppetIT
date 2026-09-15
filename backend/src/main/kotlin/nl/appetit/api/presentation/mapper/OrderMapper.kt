package nl.appetit.api.presentation.mapper

import nl.appetit.api.logic.model.Order
import nl.appetit.api.presentation.dto.order.OrderRequest
import nl.appetit.api.presentation.dto.order.OrderResponse
import nl.appetit.api.presentation.dto.table.TableResponse
import java.time.Instant

object OrderMapper {
    fun toModel(source: OrderRequest) = Order(
        tableId = source.tableId,
        restaurantId = source.restaurantId,
        staffId = source.staffId,
        status = source.status ?: "PENDING",
        claimedByStaffId = source.claimedByStaffId,
        preparedByStaffId = source.preparedByStaffId,
        totalAmount = source.totalAmount,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    fun toResponse(source: Order, table: TableResponse? = null) = OrderResponse(
        id = source.id,
        tableId = source.tableId,
        restaurantId = source.restaurantId,
        staffId = source.staffId,
        status = source.status,
        claimedByStaffId = source.claimedByStaffId,
        preparedByStaffId = source.preparedByStaffId,
        totalAmount = source.totalAmount,
        table = table,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt,
        completedAt = source.completedAt,
    )
}