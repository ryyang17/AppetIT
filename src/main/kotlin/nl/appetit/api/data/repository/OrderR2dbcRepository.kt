package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.OrderEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface OrderR2dbcRepository : ReactiveCrudRepository<OrderEntity, Int> {
    fun findByTableId(tableId: Int): Flux<OrderEntity>
    
    @Query("SELECT * FROM \"order\" WHERE status = :status")
    fun findByStatus(status: String): Flux<OrderEntity>
    
    @Query("SELECT * FROM \"order\" WHERE table_id = :tableId AND status = :status")
    fun findByTableIdAndStatus(tableId: Int, status: String): Flux<OrderEntity>
}
