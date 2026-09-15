package nl.appetit.api.presentation.dto.order_item

import java.math.BigDecimal

data class OrderItemRequest(
    val orderId: Int,
    val productId: Int?,
    val staffId: Int?,
    val quantity: Int,
    val status: String?,
    val comment: String? = null,
    val price: BigDecimal? = null
)
