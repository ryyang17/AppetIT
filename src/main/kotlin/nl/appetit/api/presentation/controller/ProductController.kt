package nl.appetit.api.presentation.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
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
import org.springframework.http.codec.multipart.FilePart
import nl.appetit.api.logic.service.FileStorageService

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Endpoints for managing products and filtering by tags")
class ProductController(
    private val productService: ProductService,
    private val tagService: TagService,
    private val fileStorageService: FileStorageService
) {

    @GetMapping
    @Operation(
        summary = "Get all products with optional tag exclusion filter",
        description = "Retrieves all products. Optionally excludes products that have any of the specified tags (useful for allergy/dietary restrictions). " +
                "If excludeTagIds is provided, products with those tags are filtered out. Each product includes its associated tags in the response."
    )
    fun list(
        @Parameter(
            description = "Comma-separated list of tag IDs to exclude. Products with any of these tags will be filtered out.",
            required = false
        )
        @RequestParam(required = false) excludeTagIds: String?
    ): Flux<ProductResponse> {
        val tagIds = excludeTagIds?.split(",")
            ?.mapNotNull { it.trim().toIntOrNull() }
            ?.takeIf { it.isNotEmpty() }
            ?: emptyList()

        val products = if (tagIds.isEmpty()) {
            productService.findAll()
        } else {
            productService.findAllExcludingTagIds(tagIds)
        }

        return products.flatMap { product ->
            tagService.getTagsByProductId(product.id!!)
                .map(TagMapper::toResponse)
                .collectList()
                .map { tags ->
                    ProductMapper.toResponse(product, tags)
                }
        }
    }

    @GetMapping("/category/{categoryId}")
    @Operation(
        summary = "Get all products in a specific category",
        description = "Retrieves all products that belong to the specified category. Each product includes its associated tags in the response."
    )
    fun listByCategory(
        @Parameter(description = "The ID of the category to filter products by", required = true)
        @PathVariable categoryId: Int
    ): Flux<ProductResponse> =
        productService.findAllByCategoryId(categoryId)
            .flatMap { product ->
                tagService.getTagsByProductId(product.id!!)
                    .map(TagMapper::toResponse)
                    .collectList()
                    .map { tags ->
                        ProductMapper.toResponse(product, tags)
                    }
            }

    @PostMapping
    @Operation(
        summary = "Create a new product",
        description = "Creates a new product with the provided details. The product will be created without any tags initially."
    )
    fun insert(
        @Parameter(description = "The product data to create", required = true)
        @RequestBody request: Mono<ProductRequest>
    ): Mono<ProductResponse> {
        return request.flatMap { product ->
            productService.save(ProductMapper.toModel(product))
                .flatMap { savedProduct ->
                    tagService.getTagsByProductId(savedProduct.id!!)
                        .map(TagMapper::toResponse)
                        .collectList()
                        .map { tags ->
                            ProductMapper.toResponse(savedProduct, tags)
                        }
                }
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing product",
        description = "Updates the product with the specified ID. Only the provided fields will be updated. Tags are not modified by this endpoint."
    )
    fun update(
        @Parameter(description = "The ID of the product to update", required = true)
        @PathVariable id: Int,
        @Parameter(description = "The updated product data", required = true)
        @RequestBody request: Mono<ProductRequest>
    ): Mono<ProductResponse> =
        request.flatMap { product ->
            productService.update(id, ProductMapper.toModel(product))
                .flatMap { updatedProduct ->
                    tagService.getTagsByProductId(updatedProduct.id!!)
                        .map(TagMapper::toResponse)
                        .collectList()
                        .map { tags ->
                            ProductMapper.toResponse(updatedProduct, tags)
                        }
                }
        }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a product by ID",
        description = "Deletes the product with the specified ID. All product-tag relationships will be automatically removed due to CASCADE delete."
    )
    fun delete(
        @Parameter(description = "The ID of the product to delete", required = true)
        @PathVariable id: Int
    ): Mono<Void> =
        productService.deleteById(id)

    @DeleteMapping
    @Operation(
        summary = "Delete all products",
        description = "Deletes all products from the database. All product-tag relationships will be automatically removed due to CASCADE delete. Use with caution!"
    )
    fun deleteAll(): Mono<Void> =
        productService.deleteAll()

    @PostMapping("/{id}/image", consumes = ["multipart/form-data"])
    @Operation(
        summary = "Upload product image",
        description = "Upload an image file for a product. Replaces the existing image. Allowed formats: jpg, png, webp. Max size: 10MB"
    )
    fun uploadImage(
        @Parameter(description = "The ID of the product", required = true)
        @PathVariable id: Int,
        @Parameter(description = "The image file to upload", required = true)
        @RequestPart("file") filePart: Mono<FilePart>
    ): Mono<ProductResponse> {
        return filePart.flatMap { file ->
            // 1. Oude afbeelding ophalen (optioneel: later verwijderen)
            productService.findById(id)
                .flatMap { product ->
                    // 2. Nieuwe afbeelding opslaan
                    fileStorageService.storeFile(id, file)
                        .flatMap { newImageUrl ->
                            // 3. Database updaten met nieuwe URL
                            productService.updateImageUrl(id, newImageUrl)
                        }
                        .flatMap { updatedProduct ->
                            // 4. Tags ophalen en response maken
                            tagService.getTagsByProductId(id)
                                .map(TagMapper::toResponse)
                                .collectList()
                                .map { tags ->
                                    ProductMapper.toResponse(updatedProduct, tags)
                                }
                        }
                }
        }
    }
}


