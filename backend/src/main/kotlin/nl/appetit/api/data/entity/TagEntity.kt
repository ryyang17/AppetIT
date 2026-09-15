package nl.appetit.api.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table(name = "tag")
data class TagEntity(
	@Id 
	@Column("tag_id")
	val id: Int? = null,

	@Column("name")
	val name: String,

	@Column("svg_icon")
	val svgIcon: String? = null,

	@Column("created_at")
	val createdAt: Instant? = null,

	@Column("updated_at")
	val updatedAt: Instant? = null
)
