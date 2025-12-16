package nl.appetit.api.presentation.mapper

import nl.appetit.api.logic.model.Table
import nl.appetit.api.presentation.dto.table.TableRequest
import nl.appetit.api.presentation.dto.table.TableResponse

object TableMapper {
    fun toModel(request: TableRequest) = Table(
        id = null,
        restaurantId = request.restaurantId,
        tableNumber = request.tableNumber,
        capacity = request.capacity,
        isActive = request.isActive
    )

    fun toResponse(model: Table) = TableResponse(
        id = model.id!!,
        restaurantId = model.restaurantId,
        tableNumber = model.tableNumber,
        capacity = model.capacity,
        isActive = model.isActive
    )
}