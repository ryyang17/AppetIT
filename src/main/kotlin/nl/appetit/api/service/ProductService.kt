package nl.appetit.api.service

import nl.appetit.api.model.Product
import nl.appetit.api.repository.ProductRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ProductService(
    private val db: ProductRepository
) {
    fun getAllProducts(): Flux<Product> =
        db.findAll()

	fun insertProduct(product: Product): Mono<Product> =
		db.save(product)

    fun deleteProductById(id: Int): Mono<Void> =
        db.deleteById(id)

    fun deleteAllProducts(): Mono<Void> =
        db.deleteAll()

	fun updateProduct(id: Int, newProduct: Product): Mono<Product> =
		db.findById(id)
			.switchIfEmpty(Mono.error(RuntimeException("Product not found with id: $id")))
			.flatMap { existing ->
				val updated = existing.copy(
					name = newProduct.name,
					price = newProduct.price,
					description = newProduct.description,
					imageUrl = newProduct.imageUrl,
					isAvailable = newProduct.isAvailable,
					categoryId = newProduct.categoryId  // Link product to a category
				)
				db.save(updated)
			}

	// Get all products that belong to a specific category
	fun getProductsByCategory(categoryId: Int): Flux<Product> =
		db.findByCategoryId(categoryId)
}


