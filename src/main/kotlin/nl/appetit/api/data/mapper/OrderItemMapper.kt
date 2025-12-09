package nl.appetit.api.data.mapper

import nl.appetit.api.data.entity.OrderItemEntity
import nl.appetit.api.logic.model.OrderItem

object OrderItemMapper {
    fun toModel(source: OrderItemEntity) = OrderItem(
        id = source.id,
        orderId = source.orderId,
        productId = source.productId,
        staffId = source.staffId,
        quantity = source.quantity,
        status = source.status ?: "PENDING",
        comment = source.comment,
        price = source.price,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt,
    )

    fun toEntity(source: OrderItem) = OrderItemEntity(
        id = source.id,
        orderId = source.orderId,
        productId = source.productId,
        staffId = source.staffId,
        quantity = source.quantity,
        status = source.status,
        comment = source.comment,
        price = source.price,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt,
    )
}