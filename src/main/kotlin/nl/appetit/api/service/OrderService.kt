package nl.appetit.api.service

import nl.appetit.api.model.Order
import nl.appetit.api.repository.OrderRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderService(
    private val db: OrderRepository
) {
    fun getAllOrders(): Flux<Order> = db.findAll()

    fun insertOrder(order: Order): Mono<Order> = db.save(order)

    fun deleteOrderById(id: Int): Mono<Void> = db.deleteById(id)

    fun deleteAllOrders(): Mono<Void> = db.deleteAll()
}
