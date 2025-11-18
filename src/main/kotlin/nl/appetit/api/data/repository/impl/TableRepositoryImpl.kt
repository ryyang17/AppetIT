package nl.appetit.api.data.repository.impl

import nl.appetit.api.data.mapper.TableMapper
import nl.appetit.api.data.repository.TableR2dbcRepository
import nl.appetit.api.logic.model.Table
import nl.appetit.api.logic.repository.TableRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class TableRepositoryImpl(
    private val tableR2dbcRepository: TableR2dbcRepository
) : TableRepository {
    override fun findById(id: Int): Mono<Table> =
        tableR2dbcRepository.findById(id)
            .map(TableMapper::toModel)

    override fun findAll(): Flux<Table> =
        tableR2dbcRepository.findAll()
            .map(TableMapper::toModel)
}

