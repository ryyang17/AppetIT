package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.OrderEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface OrderR2dbcRepository : ReactiveCrudRepository<OrderEntity, Int> {
    fun findByTableId(tableId: Int): Flux<OrderEntity>

    @Query("""          
        WITH RECURSIVE category_tree AS (
            -- Start at the base category
            SELECT c.category_id
            FROM category c
            WHERE c.name = :categoryName
                AND c.parent_id IS NULL
        
            UNION ALL
        
            -- Recursively get all child categories
            SELECT c2.category_id
            FROM category c2
            JOIN category_tree ct ON c2.parent_id = ct.category_id
        )
        SELECT DISTINCT o.*
        FROM "order" o
        JOIN order_item oi ON oi.order_id = o.order_id
        JOIN product p ON p.product_id = oi.product_id
        WHERE p.category_id IN (SELECT category_id FROM category_tree)
            AND o.restaurant_id = :restaurantId;
    """)
    fun findByRestaurantIdAndCategoryName(
        restaurantId: Int,
        categoryName: String
    ): Flux<OrderEntity>
    
    @Query("SELECT * FROM \"order\" WHERE status = :status")
    fun findByStatus(status: String): Flux<OrderEntity>
    
    @Query("SELECT * FROM \"order\" WHERE table_id = :tableId AND status = :status")
    fun findByTableIdAndStatus(tableId: Int, status: String): Flux<OrderEntity>
}
