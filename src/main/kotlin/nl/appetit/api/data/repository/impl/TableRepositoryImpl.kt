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
    private val db: TableR2dbcRepository
) : TableRepository {
    override fun findAll(): Flux<Table> =
        db.findAll()
            .map(TableMapper::toModel)

    override fun findById(id: Int): Mono<Table> =
        db.findById(id)
            .map(TableMapper::toModel)

    override fun save(table: Table): Mono<Table> =
        db.save(TableMapper.toEntity(table))
            .map(TableMapper::toModel)

    override fun findAllByRestaurantId(restaurantId: Int): Flux<Table> =
        db.findAllByRestaurantId(restaurantId)
            .map(TableMapper::toModel)

    override fun deleteById(id: Int): Mono<Void> =
        db.deleteById(id)

    override fun deleteAll(): Mono<Void> =
        db.deleteAll()
}
