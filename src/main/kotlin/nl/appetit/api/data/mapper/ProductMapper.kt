package nl.appetit.api.data.mapper

import nl.appetit.api.data.entity.ProductEntity
import nl.appetit.api.logic.model.Product

object ProductMapper {
    fun toModel(source: ProductEntity) = Product(
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

    fun toEntity(source: Product) = ProductEntity(
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