package nl.appetit.api.presentation.mapper

import jdk.internal.net.http.common.TimeSource.source
import nl.appetit.api.logic.model.Restaurant
import nl.appetit.api.logic.model.RestaurantUpdate
import nl.appetit.api.presentation.dto.restaurant.CreateRestaurantRequest
import nl.appetit.api.presentation.dto.restaurant.RestaurantResponse
import nl.appetit.api.presentation.dto.restaurant.UpdateRestaurantRequest

object RestaurantMapper {
    fun toResponse(source: Restaurant) = RestaurantResponse(
        id = source.id,
        name = source.name,
        address = source.address,
        phone = source.phone,
        email = source.email,
        isActive = source.isActive,
    )

    fun toModel(source: CreateRestaurantRequest) = Restaurant(
        name = source.name,
        address = source.address,
        phone = source.phone,
        email = source.email,
        isActive = source.active ?: true,
    )

    fun toModel(source: UpdateRestaurantRequest) = RestaurantUpdate(
        name = source.name,
        address = source.address,
        phone = source.phone,
        email = source.email,
        isActive = source.active
    )
}