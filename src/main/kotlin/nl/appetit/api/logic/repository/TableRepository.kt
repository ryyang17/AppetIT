package nl.appetit.api.logic.repository

import nl.appetit.api.logic.model.Table
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TableRepository {
    fun findById(id: Int): Mono<Table>
    fun findAll(): Flux<Table>
}

