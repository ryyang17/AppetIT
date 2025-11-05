package nl.appetit.api.logic.service

import nl.appetit.api.logic.model.Tag
import nl.appetit.api.logic.repository.ProductRepository
import nl.appetit.api.logic.repository.ProductTagRepository
import nl.appetit.api.logic.repository.TagRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val productTagRepository: ProductTagRepository,
    private val productRepository: ProductRepository
) {
    fun findAll(): Flux<Tag> = tagRepository.findAll()

    fun findById(id: Int): Mono<Tag> = tagRepository.findById(id)

    fun save(tag: Tag): Mono<Tag> = tagRepository.save(tag)

    fun update(id: Int, tag: Tag): Mono<Tag> = tagRepository.update(id, tag)

    fun deleteById(id: Int): Mono<Void> = tagRepository.deleteById(id)

    fun getTagsByProductId(productId: Int): Flux<Tag> =
        productTagRepository.findTagsByProductId(productId)

    fun addTagToProduct(productId: Int, tagId: Int): Mono<Void> {
        return productRepository.findById(productId)
            .switchIfEmpty(Mono.error(RuntimeException("Product not found with id: $productId")))
            .then(tagRepository.findById(tagId)
                .switchIfEmpty(Mono.error(RuntimeException("Tag not found with id: $tagId")))
                .then(productTagRepository.existsByProductIdAndTagId(productId, tagId))
                .flatMap { exists ->
                    if (exists) Mono.empty() else productTagRepository.addTagToProduct(productId, tagId)
                }
            )
    }

    fun removeTagFromProduct(productId: Int, tagId: Int): Mono<Void> =
        productTagRepository.removeTagFromProduct(productId, tagId)
}
