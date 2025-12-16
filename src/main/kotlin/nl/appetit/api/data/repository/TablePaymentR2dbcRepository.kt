package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.TablePaymentEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface TablePaymentR2dbcRepository : ReactiveCrudRepository<TablePaymentEntity, Int> {

    @Query(
        """
        SELECT * FROM table_payment
        ORDER BY created_at DESC
        """
    )
    fun findAllOrderByCreatedAtDesc(): Flux<TablePaymentEntity>

    @Query(
        """
        SELECT * FROM table_payment
        WHERE table_id = :tableId
        ORDER BY created_at DESC
        """
    )
    fun findByTableIdOrderByCreatedAtDesc(tableId: Int): Flux<TablePaymentEntity>
}

