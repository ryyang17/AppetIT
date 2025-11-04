package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.ProductEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface ProductR2dbcRepository : ReactiveCrudRepository<ProductEntity, Int> {
	// Find all products that belong to a specific category
	fun findAllByCategoryId(categoryId: Int): Flux<ProductEntity>
}


