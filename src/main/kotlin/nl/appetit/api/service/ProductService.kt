package nl.appetit.api.service

import nl.appetit.api.model.Product
import nl.appetit.api.repository.ProductRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ProductService(
    private val db: ProductRepository
) {
    fun getAllProducts(): Flux<Product> =
        db.findAll()

	fun insertProduct(product: Product): Mono<Product> =
		db.save(product)

    fun deleteProductById(id: Int): Mono<Void> =
        db.deleteById(id)

    fun deleteAllProducts(): Mono<Void> =
        db.deleteAll()
}


