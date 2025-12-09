package nl.appetit.api.logic.model

import java.math.BigDecimal
import java.time.Instant

data class OrderItem(
    val id: Int? = null,
    val orderId: Int,
    val productId: Int? = null,
    val staffId: Int? = null,
    val quantity: Int,
    val status: String = "PENDING",
    val comment: String? = null,
    val price: BigDecimal? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
