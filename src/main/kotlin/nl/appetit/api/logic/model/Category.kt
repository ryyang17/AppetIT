package nl.appetit.api.logic.model

import java.time.Instant

data class Category(
    val id: Long? = null,
    val name: String,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
    val parentId: Long? = null,
    val order: Int? = null
)
