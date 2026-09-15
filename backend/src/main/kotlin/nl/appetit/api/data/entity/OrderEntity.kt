package nl.appetit.api.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("order")
data class OrderEntity(
    @Id
    @Column("order_id")
    val id: Int? = null,

    @Column("table_id")
    val tableId: Int? = null,

    @Column("restaurant_id")
    val restaurantId: Int? = null,

    @Column("staff_id")
    val staffId: Int? = null,

    @Column("status")
    val status: String? = null,

    @Column("claimed_by_staff_id")
    val claimedByStaffId: Int? = null,

    @Column("prepared_by_staff_id")
    val preparedByStaffId: Int? = null,

    @Column("total_amount")
    val totalAmount: BigDecimal? = null,

    @Column("created_at")
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    val updatedAt: Instant = Instant.now(),

    @Column("completed_at")
    val completedAt: Instant? = null
)