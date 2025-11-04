package nl.appetit.api.logic.model

import java.math.BigDecimal
import java.time.Instant

data class Product(
    val id: Int? = null,
    val name: String,
    val price: BigDecimal,
    val description: String? = null,
    val imageUrl: String = "",
    val isAvailable: Boolean,
    val categoryId: Int? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
