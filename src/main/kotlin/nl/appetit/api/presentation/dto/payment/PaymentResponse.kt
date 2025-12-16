package nl.appetit.api.presentation.dto.payment

import java.math.BigDecimal
import java.time.Instant

data class PaymentResponse(
    val id: Int?,
    val orderId: Int,
    val tableId: Int?,
    val paymentMethod: String?,
    val amount: BigDecimal,
    val status: String?,
    val idealTransactionId: String?,
    val createdAt: Instant,
    val completedAt: Instant?
)

