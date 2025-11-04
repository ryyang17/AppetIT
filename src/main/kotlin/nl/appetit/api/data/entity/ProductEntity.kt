package nl.appetit.api.data.entity

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.math.BigDecimal

@Table(name = "product")
data class ProductEntity(
	@Id 
	@Column("product_id")
	val id: Int? = null,

	@Column("name")
	val name: String,

	@Column("price")
	val price: BigDecimal,

	@Column("description")
	val description: String? = null,

	@Column("image_url")
	val imageUrl: String = "",

	@Column("is_available")
	@JsonProperty("available")
	val isAvailable: Boolean,

	// Foreign key linking this product to a category (one category -> many products)
	@Column("category_id")
	val categoryId: Int? = null,

	@Column("created_at")
	val createdAt: Instant = Instant.now(),

	@Column("updated_at")
	val updatedAt: Instant = Instant.now()
)


