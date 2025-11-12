package nl.appetit.api.data.repository.impl

import nl.appetit.api.data.mapper.TagMapper
import nl.appetit.api.data.repository.ProductTagR2dbcRepository
import nl.appetit.api.logic.model.Tag
import nl.appetit.api.logic.repository.ProductTagRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class ProductTagRepositoryImpl(
    private val db: ProductTagR2dbcRepository
) : ProductTagRepository {
    override fun findTagsByProductId(productId: Int): Flux<Tag> =
        db.findTagsByProductId(productId).map(TagMapper::toModel)

    override fun addTagToProduct(productId: Int, tagId: Int): Mono<Void> =
        db.insert(productId, tagId).then()

    override fun removeTagFromProduct(productId: Int, tagId: Int): Mono<Void> =
        db.deleteByProductIdAndTagId(productId, tagId).then()

    override fun existsByProductIdAndTagId(productId: Int, tagId: Int): Mono<Boolean> =
        db.existsByProductIdAndTagId(productId, tagId)
}
