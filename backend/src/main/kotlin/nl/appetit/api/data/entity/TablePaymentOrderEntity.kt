package nl.appetit.api.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("table_payment_order")
data class TablePaymentOrderEntity(
    @Id
    @Column("id")
    val id: Int? = null,

    @Column("table_payment_id")
    val tablePaymentId: Int,

    @Column("order_id")
    val orderId: Int
)

