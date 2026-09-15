package nl.appetit.api.data.repository.impl

import nl.appetit.api.data.entity.ProductEntity
import nl.appetit.api.data.mapper.ProductMapper
import nl.appetit.api.data.repository.ProductR2dbcRepository
import nl.appetit.api.logic.model.Product
import nl.appetit.api.logic.repository.ProductRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class ProductRepositoryImpl(
    private val db: ProductR2dbcRepository
) : ProductRepository {
    override fun findAll(): Flux<Product> =
        db.findAll()
            .map(ProductMapper::toModel)

    override fun findById(id: Int): Mono<Product> =
        db.findById(id)
            .map(ProductMapper::toModel)

    override fun findAllById(ids: Iterable<Int>): Flux<Product> =
        db.findAllById(ids)
            .map(ProductMapper::toModel)

    override fun save(product: Product): Mono<Product> =
        db.save(ProductMapper.toEntity(product))
            .map(ProductMapper::toModel)

    override fun findAllByCategoryId(categoryId: Int): Flux<Product> =
        db.findAllByCategoryId(categoryId)
            .map(ProductMapper::toModel)

    override fun deleteById(id: Int): Mono<Void> =
        db.deleteById(id)

    override fun deleteAll(): Mono<Void> =
        db.deleteAll()
}