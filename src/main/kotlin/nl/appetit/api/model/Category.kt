package nl.appetit.api.model

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Table


@Table(name = "categories")
data class Category(
    @Id val id: Long? = null,
    val name: String,
    val parentId: Long? = null
)

