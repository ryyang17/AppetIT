package nl.appetit.api.logic.model

import java.math.BigDecimal
import java.time.Instant

data class Payment(
    val id: Int? = null,
    val orderId: Int,
    val paymentMethod: String? = null,
    val amount: BigDecimal,
    val status: String? = null,
    val idealTransactionId: String? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val completedAt: Instant? = null
)

