package nl.appetit.api.controller

import nl.appetit.api.model.Product
import nl.appetit.api.service.ProductService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal

data class ProductRequest(
	val name: String,
	val price: BigDecimal,
	val description: String? = null,
	val imageUrl: String? = null,
	val available: Boolean,
	val categoryId: Int? = null
)

fun ProductRequest.toEntity() = Product(
	name = this.name,
	price = this.price,
	description = this.description,
	imageUrl = this.imageUrl ?: "",
	isAvailable = this.available,
	categoryId = this.categoryId?.takeIf { it > 0 }
)

@RestController
@RequestMapping("/products")
class ProductController(
    private val service: ProductService
) {

    @GetMapping
    fun list(): Flux<Product> = service.getAllProducts()

	// Get all products for a specific category
	// Example: GET /products/category/1
	@GetMapping("/category/{categoryId}")
	fun listByCategory(@PathVariable categoryId: Int): Flux<Product> =
		service.getProductsByCategory(categoryId)

	@PostMapping
	fun insert(@RequestBody request: Mono<ProductRequest>): Mono<Product> {
		return request.flatMap { product ->
			service.insertProduct(product.toEntity())
		}
	}

	@PutMapping("/{id}")
	fun update(@PathVariable id: Int, @RequestBody request: Mono<ProductRequest>): Mono<Product> =
		request.flatMap { product ->
			service.updateProduct(id, product.toEntity())
		}

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): Mono<Void> =
        service.deleteProductById(id)

    @DeleteMapping
    fun deleteAll(): Mono<Void> =
        service.deleteAllProducts()
}


