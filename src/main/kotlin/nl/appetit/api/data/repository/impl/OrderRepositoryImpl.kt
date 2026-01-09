package nl.appetit.api.data.repository.impl

import nl.appetit.api.data.entity.OrderEntity
import nl.appetit.api.data.mapper.OrderMapper
import nl.appetit.api.data.repository.OrderR2dbcRepository
import nl.appetit.api.logic.model.Order
import nl.appetit.api.logic.repository.OrderRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class OrderRepositoryImpl(
    private val db: OrderR2dbcRepository
) : OrderRepository {
    private val logger = LoggerFactory.getLogger(OrderRepositoryImpl::class.java)

    override fun findAll(): Flux<Order> =
        db.findAll().map(OrderMapper::toModel)

    override fun findByTableId(tableId: Int): Flux<Order> =
        db.findByTableId(tableId).map(OrderMapper::toModel)

    override fun findByRestaurantIdAndCategoryName(restaurantId: Int, categoryName: String): Flux<Order> =
        db.findByRestaurantIdAndCategoryName(restaurantId, categoryName).map(OrderMapper::toModel)

    override fun findByStatus(status: String): Flux<Order> {
        logger.info("OrderRepositoryImpl.findByStatus called with status: {}", status)
        return db.findByStatus(status)
            .doOnNext { entity ->
                logger.debug("Found OrderEntity: id={}, status={}, tableId={}", entity.id, entity.status, entity.tableId)
            }
            .map(OrderMapper::toModel)
            .doOnNext { order ->
                logger.debug("Mapped to Order: id={}, status={}, tableId={}", order.id, order.status, order.tableId)
            }
            .doOnComplete {
                logger.info("Completed findByStatus for status: {}", status)
            }
            .doOnError { error ->
                logger.error("Error in findByStatus for status {}: {}", status, error.message, error)
            }
    }

    override fun findByTableIdAndStatus(tableId: Int, status: String): Flux<Order> =
        db.findByTableIdAndStatus(tableId, status).map(OrderMapper::toModel)

    override fun findById(id: Int): Mono<Order> =
        db.findById(id).map(OrderMapper::toModel)

    override fun save(order: Order): Mono<Order> =
        db.save(OrderMapper.toEntity(order)).map(OrderMapper::toModel)

    override fun deleteById(id: Int): Mono<Void> =
        db.deleteById(id)

    override fun deleteAll(): Mono<Void> =
        db.deleteAll()
}