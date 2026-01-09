package nl.appetit.api.logic.service

import nl.appetit.api.logic.model.Product
import nl.appetit.api.logic.repository.ProductRepository
import nl.appetit.api.logic.repository.ProductTagRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ProductService(
    private val db: ProductRepository,
    private val productTagRepository: ProductTagRepository,
    private val restaurantProductService: RestaurantProductService
) {
    fun findAll(): Flux<Product> =
        db.findAll()
	
	fun findById(id: Int): Mono<Product> =
        db.findById(id)

	fun save(product: Product): Mono<Product> =
		db.save(product)
			.flatMap { savedProduct ->
				// Automatically link new product to all existing restaurants
				// Products are added with isAvailable=false by default
				// Restaurants can enable them individually
				if (savedProduct.id != null) {
					restaurantProductService.linkNewProductToAllRestaurants(savedProduct.id)
						.then(Mono.just(savedProduct))
				} else {
					Mono.just(savedProduct)
				}
			}

    fun deleteById(id: Int): Mono<Void> =
        db.deleteById(id)

    fun deleteAll(): Mono<Void> =
        db.deleteAll()

	fun update(id: Int, newProduct: Product): Mono<Product> =
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
	fun findAllByCategoryId(categoryId: Int): Flux<Product> =
		db.findAllByCategoryId(categoryId)

	// Get all products that do NOT have any of the specified tags
	fun findAllExcludingTagIds(tagIds: List<Int>): Flux<Product> {
		return if (tagIds.isEmpty()) {
			findAll()
		} else {
			productTagRepository.findProductsExcludingTagIds(tagIds)
		}
	}
	fun updateImageUrl(id: Int, imageUrl: String): Mono<Product> =
		db.findById(id)
			.switchIfEmpty(Mono.error(RuntimeException("Product not found with id: $id")))
			.flatMap { existing ->
				val updated = existing.copy(imageUrl = imageUrl)
				db.save(updated)
			}
}
