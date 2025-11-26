package nl.appetit.api.logic.service

import nl.appetit.api.data.entity.OrderEntity
import nl.appetit.api.logic.model.Order
import nl.appetit.api.logic.repository.OrderRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderService(
    private val db: OrderRepository,
    private val barUpdateService: BarUpdateService
) {
    fun findAll(): Flux<Order> = db.findAll()

    fun save(order: Order): Mono<Order> =
        db.save(order)
            .doOnNext { savedOrder ->
                barUpdateService.pushUpdate("1", "${savedOrder.id}")
            }

    fun deleteById(id: Int): Mono<Void> = db.deleteById(id)

    fun deleteAll(): Mono<Void> = db.deleteAll()
}
