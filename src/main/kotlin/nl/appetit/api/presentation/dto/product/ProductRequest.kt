package nl.appetit.api.presentation.dto.product

import java.math.BigDecimal

data class ProductRequest(
    val name: String,
    val price: BigDecimal,
    val description: String? = null,
    val imageUrl: String? = null,
    val available: Boolean,
    val categoryId: Int? = null
)
