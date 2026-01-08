package nl.appetit.api.presentation.dto.payment

data class PaymentRequest(
    val paymentMethod: String? = "UNKNOWN"
)

