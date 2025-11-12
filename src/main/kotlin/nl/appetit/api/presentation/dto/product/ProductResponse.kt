package nl.appetit.api.presentation.dto.product

import nl.appetit.api.presentation.dto.tag.TagResponse
import java.math.BigDecimal
import java.time.Instant

data class ProductResponse(
    val id: Int? = null,
    val name: String,
    val price: BigDecimal,
    val description: String? = null,
    val imageUrl: String = "",
    val isAvailable: Boolean,
    val categoryId: Int? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
    val tags: List<TagResponse>? = null
)
