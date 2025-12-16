package nl.appetit.api.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("table_payment")
data class TablePaymentEntity(
    @Id
    @Column("table_payment_id")
    val id: Int? = null,

    @Column("table_id")
    val tableId: Int? = null,

    @Column("total_amount")
    val totalAmount: BigDecimal,

    @Column("payment_method")
    val paymentMethod: String? = null,

    @Column("status")
    val status: String? = null,

    @Column("created_at")
    val createdAt: Instant = Instant.now()
)

