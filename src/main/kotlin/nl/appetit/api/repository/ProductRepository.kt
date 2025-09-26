package nl.appetit.api.repository

import nl.appetit.api.model.Product
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ProductRepository : ReactiveCrudRepository<Product, Int> { }


