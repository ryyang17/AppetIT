package nl.appetit.api.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("restaurant")
data class RestaurantEntity(
    @Column("restaurant_id")
    @Id
    val id: Int? = null,
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    @Column("is_active")
    val isActive: Boolean,
    @Column("created_at")
    val createdAt: LocalDateTime? = null,
    @Column("updated_at")
    val updatedAt: LocalDateTime? = null,
)
