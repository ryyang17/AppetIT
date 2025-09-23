package nl.appetit.api.repository

import nl.appetit.api.model.Category
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface CategoryRepository : ReactiveCrudRepository<Category, Long> { }