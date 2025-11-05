package nl.appetit.api.presentation.mapper

import nl.appetit.api.logic.model.Product
import nl.appetit.api.presentation.dto.product.ProductRequest
import nl.appetit.api.presentation.dto.product.ProductResponse

object ProductMapper {
    fun toModel(source: ProductRequest) = Product(
        name = source.name,
        price = source.price,
        description = source.description,
        imageUrl = source.imageUrl ?: "",
        isAvailable = source.available,
        categoryId = source.categoryId?.takeIf { it > 0 }
    )

    fun toResponse(source: Product) = ProductResponse(
        id = source.id,
        name = source.name,
        price = source.price,
        description = source.description,
        imageUrl = source.imageUrl,
        isAvailable = source.isAvailable,
        categoryId = source.categoryId,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt,
    )
}