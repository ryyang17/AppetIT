package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.OrderEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface OrderR2dbcRepository : ReactiveCrudRepository<OrderEntity, Int> {
    fun findByTableId(tableId: Int): Flux<OrderEntity>
}
