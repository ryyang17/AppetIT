// ...new file...
package nl.appetit.api.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("table")
data class TableEntity(
    @Id
    @Column("table_id")
    val id: Int? = null,

    @Column("restaurant_id")
    val restaurantId: Int,

    @Column("table_number")
    val tableNumber: Int,

    @Column("capacity")
    val capacity: Int? = null,

    @Column("is_active")
    val isActive: Boolean = true,

    @Column("created_at")
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    val updatedAt: Instant = Instant.now()
)

