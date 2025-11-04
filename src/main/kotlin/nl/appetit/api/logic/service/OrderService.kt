package nl.appetit.api.logic.service

import nl.appetit.api.data.entity.OrderEntity
import nl.appetit.api.logic.model.Order
import nl.appetit.api.logic.repository.OrderRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderService(
    private val db: OrderRepository
) {
    fun findAll(): Flux<Order> = db.findAll()

    fun save(order: Order): Mono<Order> = db.save(order)

    fun deleteById(id: Int): Mono<Void> = db.deleteById(id)

    fun deleteAll(): Mono<Void> = db.deleteAll()
}
