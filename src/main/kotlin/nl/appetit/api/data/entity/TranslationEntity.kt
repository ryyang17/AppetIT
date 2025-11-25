package nl.appetit.api.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table(name = "translations")
data class TranslationEntity(
    @Id
    @Column("translation_id")
    val id: Long? = null,

    @Column("entity_type")
    val entityType: String,

    @Column("entity_id")
    val entityId: Long,

    @Column("language")
    val language: String,

    @Column("translation")
    val translation: String?,

    @Column("created_at")
    val createdAt: Instant? = null,

    @Column("updated_at")
    val updatedAt: Instant? = null
)