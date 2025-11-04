package nl.appetit.api.data.entity

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant


@Table(name = "category")
data class CategoryEntity(
	@Id 
	@Column("category_id")
	val id: Long? = null,
    
	@Column("name")
	val name: String,
    
	@Column("created_at")
	val createdAt: Instant? = null,

	@Column("updated_at")
	val updatedAt: Instant? = null,

    @Column("parent_id")
    val parentId: Long? = null,

	@Column("category_order")
	val order: Int? = null
)
