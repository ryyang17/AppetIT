package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.TagEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TagR2dbcRepository : ReactiveCrudRepository<TagEntity, Int>
