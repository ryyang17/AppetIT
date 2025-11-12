package nl.appetit.api.logic.repository

import nl.appetit.api.logic.model.Product
import nl.appetit.api.logic.model.Tag
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ProductTagRepository {
    fun findTagsByProductId(productId: Int): Flux<Tag>
    fun addTagToProduct(productId: Int, tagId: Int): Mono<Void>
    fun removeTagFromProduct(productId: Int, tagId: Int): Mono<Void>
    fun existsByProductIdAndTagId(productId: Int, tagId: Int): Mono<Boolean>
    fun findProductsExcludingTagIds(tagIds: List<Int>): Flux<Product>
}
