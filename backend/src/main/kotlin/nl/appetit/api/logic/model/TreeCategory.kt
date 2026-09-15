package nl.appetit.api.logic.model

import java.time.Instant

data class TreeCategory(
    val id: Long,
    val name: String,
    val parentId: Long? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
    val children: List<TreeCategory> = emptyList()
)

fun Category.toTreeCategory(children: List<TreeCategory>) = TreeCategory(
    id = this.id ?: throw IllegalStateException("No id"),
    name = this.name,
    parentId = this.parentId,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    children = children
)
