package nl.appetit.api.presentation.dto.order_item

import java.time.Instant

data class OrderItemResponse(
    val id: Int? = null,
    val orderId: Int,
    val productId: Int? = null,
    val staffId: Int? = null,
    val quantity: Int,
    val status: String = "PENDING",
    val createdAt: Instant,
    val updatedAt: Instant
)
