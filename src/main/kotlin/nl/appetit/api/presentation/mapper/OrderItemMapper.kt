package nl.appetit.api.presentation.mapper

import nl.appetit.api.logic.model.OrderItem
import nl.appetit.api.presentation.dto.order_item.OrderItemRequest
import nl.appetit.api.presentation.dto.order_item.OrderItemResponse

object OrderItemMapper {
    fun toModel(source: OrderItemRequest) = OrderItem(
        orderId = source.orderId,
        productId = source.productId,
        staffId = source.staffId,
        quantity = source.quantity,
        status = "PENDING"
    )

    fun toResponse(source: OrderItem) = OrderItemResponse(
        id = source.orderId,
        orderId = source.orderId,
        productId = source.productId,
        staffId = source.staffId,
        quantity = source.quantity,
        status = source.status,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt
    )
}