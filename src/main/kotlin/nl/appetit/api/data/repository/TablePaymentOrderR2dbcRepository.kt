package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.TablePaymentOrderEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface TablePaymentOrderR2dbcRepository : ReactiveCrudRepository<TablePaymentOrderEntity, Int> {
    fun findByTablePaymentId(tablePaymentId: Int): Flux<TablePaymentOrderEntity>
}

