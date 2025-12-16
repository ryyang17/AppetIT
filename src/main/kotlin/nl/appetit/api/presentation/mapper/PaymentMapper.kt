package nl.appetit.api.presentation.mapper

import nl.appetit.api.logic.model.Order
import nl.appetit.api.logic.model.Payment
import nl.appetit.api.presentation.dto.payment.PaymentResponse

object PaymentMapper {

    fun toResponse(payment: Payment, order: Order?): PaymentResponse =
        PaymentResponse(
            id = payment.id,
            orderId = payment.orderId,
            tableId = order?.tableId,
            paymentMethod = payment.paymentMethod,
            amount = payment.amount,
            status = payment.status,
            idealTransactionId = payment.idealTransactionId,
            createdAt = payment.createdAt,
            completedAt = payment.completedAt
        )
}

