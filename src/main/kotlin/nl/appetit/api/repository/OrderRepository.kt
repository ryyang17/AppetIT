package nl.appetit.api.repository

import nl.appetit.api.model.Order
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface OrderRepository : ReactiveCrudRepository<Order, Int>
