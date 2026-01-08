package nl.appetit.api.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("payment")
data class PaymentEntity(
    @Id
    @Column("payment_id")
    val id: Int? = null,

    @Column("order_id")
    val orderId: Int,

    @Column("payment_method")
    val paymentMethod: String? = null,

    @Column("amount")
    val amount: BigDecimal,

    @Column("status")
    val status: String? = null,

    @Column("ideal_transaction_id")
    val idealTransactionId: String? = null,

    @Column("created_at")
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    val updatedAt: Instant = Instant.now(),

    @Column("completed_at")
    val completedAt: Instant? = null
)

