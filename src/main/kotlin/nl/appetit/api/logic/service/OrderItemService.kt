package nl.appetit.api.logic.service

import nl.appetit.api.logic.model.OrderItem
import nl.appetit.api.logic.model.Product
import nl.appetit.api.logic.repository.OrderItemRepository
import nl.appetit.api.logic.repository.ProductRepository
import nl.appetit.api.presentation.dto.order_item.OrderItemResponse
import nl.appetit.api.presentation.mapper.OrderItemMapper
import nl.appetit.api.presentation.mapper.ProductMapper
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderItemService(
    private val db: OrderItemRepository,
    private val productRepository: ProductRepository
) {
    fun getAllOrderItems(): Flux<OrderItem> = db.findAll()

    fun getItemsByOrder(orderId: Int): Flux<OrderItem> = db.findAllByOrderId(orderId)

    /**
     * Get all order items with product data included using batch fetching to avoid N+1 queries.
     * Performance: 2 queries instead of N+1 (1 for order items, 1 for all products)
     */
    fun getAllOrderItemsWithProducts(): Flux<OrderItemResponse> =
        db.findAll()
            .collectList() // Collect all order items first
            .flatMapMany { orderItems ->
                // Extract unique product IDs
                val productIds = orderItems.mapNotNull { it.productId }.distinct()
                
                if (productIds.isEmpty()) {
                    // No products to fetch, return items without product data
                    Flux.fromIterable(orderItems.map { 
                        OrderItemMapper.toResponse(it, null) 
                    })
                } else {
                    // Batch fetch all products in one query
                    productRepository.findAllById(productIds)
                        .collectList() // Collect all products
                        .flatMapMany { products ->
                            // Create map: productId -> Product
                            val productMap = products.associateBy { it.id }
                            
                            // Combine order items with products
                            Flux.fromIterable(orderItems).map { orderItem ->
                                val product = orderItem.productId?.let { productMap[it] }
                                OrderItemMapper.toResponse(
                                    orderItem,
                                    product?.let { ProductMapper.toResponse(it) }
                                )
                            }
                        }
                }
            }

    /**
     * Get order items for a specific order with product data included using batch fetching.
     * Performance: 2 queries instead of N+1 (1 for order items, 1 for all products)
     */
    fun getItemsByOrderWithProducts(orderId: Int): Flux<OrderItemResponse> =
        db.findAllByOrderId(orderId)
            .collectList() // Collect all order items first
            .flatMapMany { orderItems ->
                // Extract unique product IDs
                val productIds = orderItems.mapNotNull { it.productId }.distinct()
                
                if (productIds.isEmpty()) {
                    // No products to fetch, return items without product data
                    Flux.fromIterable(orderItems.map { 
                        OrderItemMapper.toResponse(it, null) 
                    })
                } else {
                    // Batch fetch all products in one query
                    productRepository.findAllById(productIds)
                        .collectList() // Collect all products
                        .flatMapMany { products ->
                            // Create map: productId -> Product
                            val productMap = products.associateBy { it.id }
                            
                            // Combine order items with products
                            Flux.fromIterable(orderItems).map { orderItem ->
                                val product = orderItem.productId?.let { productMap[it] }
                                OrderItemMapper.toResponse(
                                    orderItem,
                                    product?.let { ProductMapper.toResponse(it) }
                                )
                            }
                        }
                }
            }

    fun insertOrderItem(item: OrderItem): Mono<OrderItem> = db.save(item)

    fun deleteOrderItemById(id: Int): Mono<Void> = db.deleteById(id)

    fun deleteAllOrderItems(): Mono<Void> = db.deleteAll()
}
