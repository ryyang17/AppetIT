package nl.appetit.api.data.mapper

import nl.appetit.api.data.entity.PaymentEntity
import nl.appetit.api.logic.model.Payment

object PaymentMapper {
    fun toEntity(source: Payment) = PaymentEntity(
        id = source.id,
        orderId = source.orderId,
        paymentMethod = source.paymentMethod,
        amount = source.amount,
        status = source.status,
        idealTransactionId = source.idealTransactionId,
        createdAt = source.createdAt,
        updatedAt = source.updatedAt,
        completedAt = source.completedAt
    )

    fun toModel(entity: PaymentEntity) = Payment(
        id = entity.id,
        orderId = entity.orderId,
        paymentMethod = entity.paymentMethod,
        amount = entity.amount,
        status = entity.status,
        idealTransactionId = entity.idealTransactionId,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
        completedAt = entity.completedAt
    )
}

