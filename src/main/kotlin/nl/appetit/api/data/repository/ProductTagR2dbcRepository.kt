package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.TagEntity
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ProductTagR2dbcRepository : ReactiveCrudRepository<TagEntity, Int> {
	@Query("SELECT t.tag_id, t.name, t.svg_icon, t.created_at, t.updated_at FROM tag t INNER JOIN product_tag pt ON t.tag_id = pt.tag_id WHERE pt.product_id = :productId")
	fun findTagsByProductId(productId: Int): Flux<TagEntity>

	@Modifying
	@Query("INSERT INTO product_tag (product_id, tag_id, created_at) VALUES (:productId, :tagId, NOW())")
	fun insert(productId: Int, tagId: Int): Mono<Int>

	@Modifying
	@Query("DELETE FROM product_tag WHERE product_id = :productId AND tag_id = :tagId")
	fun deleteByProductIdAndTagId(productId: Int, tagId: Int): Mono<Int>

	@Query("SELECT EXISTS(SELECT 1 FROM product_tag WHERE product_id = :productId AND tag_id = :tagId)")
	fun existsByProductIdAndTagId(productId: Int, tagId: Int): Mono<Boolean>
}
