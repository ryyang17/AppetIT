package nl.appetit.api.presentation.mapper

import nl.appetit.api.logic.model.OrderItem
import nl.appetit.api.presentation.dto.order_item.OrderItemRequest
import nl.appetit.api.presentation.dto.order_item.OrderItemResponse
import nl.appetit.api.presentation.dto.product.ProductResponse

object OrderItemMapper {
    fun toModel(source: OrderItemRequest) = OrderItem(
        orderId = source.orderId,
        productId = source.productId,
        staffId = source.staffId,
        quantity = source.quantity,
        status = "PENDING",
        comment = source.comment,
        price = source.price
    )

    fun toResponse(source: OrderItem, product: ProductResponse? = null) = OrderItemResponse(
        id = source.id,
        orderId = source.orderId,
        productId = source.productId,
        staffId = source.staffId,
        quantity = source.quantity,
        status = source.status,
        comment = source.comment,
        price = source.price,
        product = product,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt
    )
}