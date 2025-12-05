package nl.appetit.api.presentation.dto.order

data class OrderPatch(
    val status: String? = null,
    val claimedByStaffId: Int? = null,
    val preparedByStaffId: Int? = null
)