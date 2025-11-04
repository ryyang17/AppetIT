package nl.appetit.api.logic.model

import java.math.BigDecimal
import java.time.Instant

data class Order(
    val id: Int? = null,
    val tableId: Int? = null,
    val restaurantId: Int? = null,
    val staffId: Int? = null,
    val status: String? = null,
    val claimedByStaffId: Int? = null,
    val preparedByStaffId: Int? = null,
    val totalAmount: BigDecimal? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val completedAt: Instant? = null
)
