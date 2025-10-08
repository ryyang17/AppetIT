package nl.appetit.api.model

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant


@Table(name = "category")
data class Category(
	@Id 
	@Column("category_id")
	val id: Long? = null,
    
	@Column("name")
	val name: String,
    
	@Column("created_at")
	val createdAt: Instant? = null,

	@Column("updated_at")
	val updatedAt: Instant? = null,

	@Column("category_order")
	val order: Int? = null
)
