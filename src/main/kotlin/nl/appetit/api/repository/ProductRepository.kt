package nl.appetit.api.repository

import nl.appetit.api.model.Product
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface ProductRepository : ReactiveCrudRepository<Product, Int> {
	// Find all products that belong to a specific category
	fun findByCategoryId(categoryId: Int): Flux<Product>
}


