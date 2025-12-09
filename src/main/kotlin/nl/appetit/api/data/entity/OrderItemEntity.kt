package nl.appetit.api.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("order_item")
data class OrderItemEntity(
    @Id
    @Column("order_item_id")
    val id: Int? = null,

    @Column("order_id")
    val orderId: Int,

    @Column("product_id")
    val productId: Int? = null,

    @Column("staff_id")
    val staffId: Int? = null,

    @Column("quantity")
    val quantity: Int,

    @Column("status")
    val status: String? = "PENDING",

    @Column("comment")
    val comment: String? = null,

    @Column("price")
    val price: BigDecimal? = null,

    @Column("created_at")
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    val updatedAt: Instant = Instant.now()
)
