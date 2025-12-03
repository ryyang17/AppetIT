package nl.appetit.api.data.entity

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table(name = "staff")
data class EmployeeEntity(
    @Id
    @Column("staff_id")
    val id: Int? = null,

    @Column("personnel_number")
    val personnelNumber: Int? = null,

    @Column("restaurant_id")
    val restaurantId: Int? = null,

    @Column("first_name")
    val firstName: String,

    @Column("last_name")
    val lastName: String,

    @Column("role")
    val role: String,

    @Column("is_active")
    @JsonProperty("active")
    val active: Boolean = true,

    @Column("created_at")
    val createdAt: Instant? = null,

    @Column("updated_at")
    val updatedAt: Instant? = null
)
