package nl.appetit.api.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table(name = "restaurant_product")
data class RestaurantProductEntity(
    @Id
    @Column("id")
    val id: Int? = null,

    @Column("restaurant_id")
    val restaurantId: Int,

    @Column("product_id")
    val productId: Int,

    @Column("custom_price")
    val customPrice: BigDecimal? = null,

    @Column("is_available")
    val isAvailable: Boolean,

    @Column("created_at")
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    val updatedAt: Instant = Instant.now()
)
