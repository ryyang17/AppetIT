package nl.appetit.api.presentation.dto.order

import java.math.BigDecimal

data class OrderRequest(
    val tableId: Int,
    val restaurantId: Int?,
    val staffId: Int?,
    val status: String?,
    val claimedByStaffId: Int?,
    val preparedByStaffId: Int?,
    val totalAmount: BigDecimal?
)