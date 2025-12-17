package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.PaymentEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface PaymentR2dbcRepository : ReactiveCrudRepository<PaymentEntity, Int> {

    fun findByOrderId(orderId: Int): Flux<PaymentEntity>

    @Query(
        """
        SELECT p.* FROM payment p
        JOIN "order" o ON p.order_id = o.order_id
        WHERE o.table_id = :tableId
        """,
    )
    fun findByTableId(tableId: Int): Flux<PaymentEntity>
}

