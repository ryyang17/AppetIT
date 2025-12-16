package nl.appetit.api.presentation.mapper

import nl.appetit.api.data.entity.TablePaymentEntity
import nl.appetit.api.logic.model.Order
import nl.appetit.api.presentation.dto.payment.PaymentResponse
import nl.appetit.api.presentation.dto.order.OrderResponse

object PaymentMapper {

    fun toResponse(tablePayment: TablePaymentEntity, orders: List<OrderResponse>): PaymentResponse =
        PaymentResponse(
            id = tablePayment.id,
            tableId = tablePayment.tableId,
            totalAmount = tablePayment.totalAmount,
            paymentMethod = tablePayment.paymentMethod,
            status = tablePayment.status,
            createdAt = tablePayment.createdAt,
            orders = orders
        )
}

