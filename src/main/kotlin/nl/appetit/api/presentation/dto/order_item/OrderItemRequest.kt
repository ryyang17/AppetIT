package nl.appetit.api.presentation.dto.order_item

data class OrderItemRequest(
    val orderId: Int,
    val productId: Int?,
    val staffId: Int?,
    val quantity: Int,
    val status: String?
)
