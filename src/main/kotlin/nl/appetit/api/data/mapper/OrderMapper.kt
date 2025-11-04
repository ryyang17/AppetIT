package nl.appetit.api.data.mapper

import nl.appetit.api.data.entity.OrderEntity
import nl.appetit.api.logic.model.Order

object OrderMapper {
    fun toEntity(source: Order) = OrderEntity(
        id = source.id,
        tableId = source.tableId,
        restaurantId = source.restaurantId,
        staffId = source.staffId,
        status = source.status,
        claimedByStaffId = source.claimedByStaffId,
        preparedByStaffId = source.preparedByStaffId,
        totalAmount = source.totalAmount,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt,
        completedAt = source.completedAt,
    )

    fun toModel(orderEntity: OrderEntity) = Order(
        id = orderEntity.id,
        tableId = orderEntity.tableId,
        restaurantId = orderEntity.restaurantId,
        staffId = orderEntity.staffId,
        status = orderEntity.status,
        claimedByStaffId = orderEntity.claimedByStaffId,
        preparedByStaffId = orderEntity.preparedByStaffId,
        totalAmount = orderEntity.totalAmount,
        createdAt = orderEntity.createdAt,
        updatedAt = orderEntity.updatedAt,
        completedAt = orderEntity.completedAt,
    )
}