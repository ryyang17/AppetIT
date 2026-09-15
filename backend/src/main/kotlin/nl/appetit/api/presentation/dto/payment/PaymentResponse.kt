package nl.appetit.api.presentation.dto.payment

import nl.appetit.api.presentation.dto.order.OrderResponse
import java.math.BigDecimal
import java.time.Instant

/**
 * Represents a payment per table (TablePayment) including all related orders.
 */
data class PaymentResponse(
    val id: Int?,
    val tableId: Int?,
    val totalAmount: BigDecimal,
    val paymentMethod: String?,
    val status: String?,
    val createdAt: Instant,
    val orders: List<OrderResponse>
)

