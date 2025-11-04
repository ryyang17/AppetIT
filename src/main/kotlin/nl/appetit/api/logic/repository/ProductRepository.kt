package nl.appetit.api.logic.repository

import nl.appetit.api.data.entity.ProductEntity
import nl.appetit.api.logic.model.Product
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ProductRepository {
    fun findAll(): Flux<Product>
    fun findById(id: Int): Mono<Product>
    fun save(product: Product): Mono<Product>
    fun findAllByCategoryId(categoryId: Int): Flux<Product>
    fun deleteById(id: Int): Mono<Void>
    fun deleteAll(): Mono<Void>
}