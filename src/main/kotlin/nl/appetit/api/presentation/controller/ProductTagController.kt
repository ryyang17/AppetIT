package nl.appetit.api.presentation.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import nl.appetit.api.logic.service.TagService
import nl.appetit.api.presentation.dto.tag.TagResponse
import nl.appetit.api.presentation.mapper.TagMapper
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/product-tags")
@Tag(name = "Product Tags", description = "Endpoints for managing product-tag relationships")
class ProductTagController(
    private val tagService: TagService
) {

    @GetMapping("/product/{productId}/tags")
    @Operation(
        summary = "Get tags for product",
        description = "Retrieves all tags that are assigned to the specified product"
    )
    fun getTagsByProductId(
        @Parameter(description = "The ID of the product to get tags for", required = true)
        @PathVariable productId: Int
    ): Flux<TagResponse> =
        tagService.getTagsByProductId(productId)
            .map(TagMapper::toResponse)

    @PostMapping("/product/{productId}/tag/{tagId}")
    @Operation(
        summary = "Add tag to product",
        description = "Creates a relationship between a product and a tag. If the relationship already exists, no error is thrown."
    )
    fun addTagToProduct(
        @Parameter(description = "The ID of the product to add a tag to", required = true)
        @PathVariable productId: Int,
        @Parameter(description = "The ID of the tag to assign to the product", required = true)
        @PathVariable tagId: Int
    ): Mono<Void> =
        tagService.addTagToProduct(productId, tagId)

    @DeleteMapping("/product/{productId}/tag/{tagId}")
    @Operation(
        summary = "Remove tag from product",
        description = "Deletes the relationship between a product and a tag. If the relationship does not exist, no error is thrown."
    )
    fun removeTagFromProduct(
        @Parameter(description = "The ID of the product to remove a tag from", required = true)
        @PathVariable productId: Int,
        @Parameter(description = "The ID of the tag to remove from the product", required = true)
        @PathVariable tagId: Int
    ): Mono<Void> =
        tagService.removeTagFromProduct(productId, tagId)
}

