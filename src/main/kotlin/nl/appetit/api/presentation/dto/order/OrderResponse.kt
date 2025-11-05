package nl.appetit.api.presentation.dto.order

import java.math.BigDecimal
import java.time.Instant

data class OrderResponse(
    val id: Int? = null,
    val tableId: Int? = null,
    val restaurantId: Int? = null,
    val staffId: Int? = null,
    val status: String? = null,
    val claimedByStaffId: Int? = null,
    val preparedByStaffId: Int? = null,
    val totalAmount: BigDecimal? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
    val completedAt: Instant? = null
)
