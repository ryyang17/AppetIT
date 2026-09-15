package nl.appetit.api.presentation.dto.order_item

import nl.appetit.api.presentation.dto.product.ProductResponse
import java.math.BigDecimal
import java.time.Instant

data class OrderItemResponse(
    val id: Int? = null,
    val orderId: Int,
    val productId: Int? = null,
    val staffId: Int? = null,
    val quantity: Int,
    val status: String = "PENDING",
    val comment: String? = null,
    val price: BigDecimal? = null,
    val product: ProductResponse? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)
