package nl.appetit.api.presentation.controller

import nl.appetit.api.data.entity.ProductEntity
import nl.appetit.api.logic.model.Product
import nl.appetit.api.logic.service.ProductService
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

fun ProductRequest.toModel() = Product(
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
    private val productService: ProductService
) {

    @GetMapping
    fun list(): Flux<Product> = productService.findAll()

	// Get all products for a specific category
	// Example: GET /products/category/1
	@GetMapping("/category/{categoryId}")
	fun listByCategory(@PathVariable categoryId: Int): Flux<Product> =
		productService.findAllByCategoryId(categoryId)

	@PostMapping
	fun insert(@RequestBody request: Mono<ProductRequest>): Mono<Product> {
		return request.flatMap { product ->
			productService.save(product.toModel())
		}
	}

	@PutMapping("/{id}")
	fun update(@PathVariable id: Int, @RequestBody request: Mono<ProductRequest>): Mono<Product> =
		request.flatMap { product ->
			productService.update(id, product.toModel())
		}

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): Mono<Void> =
        productService.deleteById(id)

    @DeleteMapping
    fun deleteAll(): Mono<Void> =
        productService.deleteAll()
}


