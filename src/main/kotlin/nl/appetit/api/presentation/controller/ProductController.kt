package nl.appetit.api.presentation.controller

import nl.appetit.api.data.entity.ProductEntity
import nl.appetit.api.logic.model.Product
import nl.appetit.api.logic.service.ProductService
import nl.appetit.api.logic.service.TagService
import nl.appetit.api.presentation.dto.product.ProductRequest
import nl.appetit.api.presentation.dto.product.ProductResponse
import nl.appetit.api.presentation.dto.tag.TagResponse
import nl.appetit.api.presentation.mapper.ProductMapper
import nl.appetit.api.presentation.mapper.TagMapper
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
    private val tagService: TagService
) {

    @GetMapping
    fun list(): Flux<ProductResponse> = productService.findAll()
        .map(ProductMapper::toResponse)

	// Get all products for a specific category
	// Example: GET /products/category/1
	@GetMapping("/category/{categoryId}")
	fun listByCategory(@PathVariable categoryId: Int): Flux<ProductResponse> =
		productService.findAllByCategoryId(categoryId)
            .map(ProductMapper::toResponse)

	@PostMapping
	fun insert(@RequestBody request: Mono<ProductRequest>): Mono<ProductResponse> {
		return request.flatMap { product ->
			productService.save(ProductMapper.toModel(product))
                .map(ProductMapper::toResponse)
		}
	}

	@PutMapping("/{id}")
	fun update(@PathVariable id: Int, @RequestBody request: Mono<ProductRequest>): Mono<ProductResponse> =
		request.flatMap { product ->
			productService.update(id, ProductMapper.toModel(product))
                .map(ProductMapper::toResponse)
		}

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): Mono<Void> =
        productService.deleteById(id)

    @DeleteMapping
    fun deleteAll(): Mono<Void> =
        productService.deleteAll()

    @GetMapping("/{productId}/tags")
    fun getTagsByProductId(@PathVariable productId: Int): Flux<TagResponse> =
        tagService.getTagsByProductId(productId)
            .map(TagMapper::toResponse)

    @PostMapping("/{productId}/tags/{tagId}")
    fun addTagToProduct(
        @PathVariable productId: Int,
        @PathVariable tagId: Int
    ): Mono<Void> =
        tagService.addTagToProduct(productId, tagId)

    @DeleteMapping("/{productId}/tags/{tagId}")
    fun removeTagFromProduct(
        @PathVariable productId: Int,
        @PathVariable tagId: Int
    ): Mono<Void> =
        tagService.removeTagFromProduct(productId, tagId)
}


