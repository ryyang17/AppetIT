package nl.appetit.api.data.mapper

import nl.appetit.api.data.entity.RestaurantEntity
import nl.appetit.api.logic.model.Restaurant

object RestaurantMapper {
    fun toModel(source: RestaurantEntity) = Restaurant(
        id = source.id,
        name = source.name,
        address = source.address,
        phone = source.phone,
        email = source.email,
        isActive = source.isActive,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt
    )

    fun toEntity(source: Restaurant) = RestaurantEntity(
        id = source.id,
        name = source.name,
        address = source.address,
        phone = source.phone,
        email = source.email,
        isActive = source.isActive,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt
    )
}