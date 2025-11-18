package nl.appetit.api.data.mapper

import nl.appetit.api.logic.model.Table

object TableMapper {
    fun toModel(entity: nl.appetit.api.data.entity.TableEntity) = Table(
        id = entity.id,
        restaurantId = entity.restaurantId,
        tableNumber = entity.tableNumber,
        capacity = entity.capacity,
        isActive = entity.isActive
    )

    fun toEntity(model: Table) = nl.appetit.api.data.entity.TableEntity(
        id = model.id,
        restaurantId = model.restaurantId,
        tableNumber = model.tableNumber,
        capacity = model.capacity,
        isActive = model.isActive
    )
}
