package nl.appetit.api.presentation.dto.product

import com.fasterxml.jackson.annotation.JsonAlias
import java.math.BigDecimal

data class ProductRequest(
    val name: String,
    val price: BigDecimal,
    val description: String? = null,
    val imageUrl: String? = null,
    @JsonAlias("available")
    val isAvailable: Boolean,
    val categoryId: Int? = null
)
