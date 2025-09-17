package nl.appetit.api.controller

import nl.appetit.api.model.Category
import nl.appetit.api.repository.CategoryRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val repository: CategoryRepository
) {

    @GetMapping
    fun list(): Flux<Category> = repository.findAll()

}