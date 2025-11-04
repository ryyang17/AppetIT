package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.OrderEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface OrderR2dbcRepository : ReactiveCrudRepository<OrderEntity, Int>
