package nl.appetit.api.config

import nl.appetit.api.data.entity.OrderEntity
import nl.appetit.api.data.entity.OrderItemEntity
import nl.appetit.api.data.entity.EmployeeEntity
import nl.appetit.api.data.repository.OrderR2dbcRepository
import nl.appetit.api.data.repository.OrderItemR2dbcRepository
import nl.appetit.api.data.repository.ProductR2dbcRepository
import nl.appetit.api.data.repository.TableR2dbcRepository
import nl.appetit.api.data.repository.EmployeeR2dbcRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.context.annotation.Profile
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.ZoneId
import java.time.LocalDate
import kotlin.random.Random

/**
 * Seeds test orders with different statuses for development/testing.
 * This runs after the main DataSeeder to ensure products and tables exist.
 */
@Profile("!prod")
@Order(2) // Run after main DataSeeder (which has default order)
@Component
class OrderTestDataSeeder(
    private val orderR2dbcRepository: OrderR2dbcRepository,
    private val orderItemR2dbcRepository: OrderItemR2dbcRepository,
    private val productR2dbcRepository: ProductR2dbcRepository,
    private val tableR2dbcRepository: TableR2dbcRepository,
    private val employeeR2dbcRepository: EmployeeR2dbcRepository
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(OrderTestDataSeeder::class.java)

    init {
        logger.info("OrderTestDataSeeder component initialized and ready to run")
    }

    override fun run(vararg args: String) {
        try {
            logger.info("=".repeat(60))
            logger.info("OrderTestDataSeeder.run() called - Starting order test data seeding...")
            logger.info("=".repeat(60))

            // Get available products, tables, and employees
            logger.info("Fetching products, tables, and employees...")
            val products = productR2dbcRepository.findAll().collectList().block() ?: emptyList()
            val tables = tableR2dbcRepository.findAll().collectList().block() ?: emptyList()
            val employees = employeeR2dbcRepository.findAll().collectList().block() ?: emptyList()

            logger.info("Found ${products.size} products, ${tables.size} tables, and ${employees.size} employees")

            // Group employees by role for easier assignment
            val waiters = employees.filter { it.role == "WAITER" || it.role == "CASHIER" }
            val kitchenChefs = employees.filter { it.role == "KITCHEN_CHEF" }
            val bartenders = employees.filter { it.role == "BARTENDER" }

            logger.info("Employees by role: ${waiters.size} waiters/cashiers, ${kitchenChefs.size} kitchen chefs, ${bartenders.size} bartenders")

            if (products.isEmpty()) {
                logger.error("No products found. Skipping order test data seeding.")
                return
            }

            if (tables.isEmpty()) {
                logger.error("No tables found. Skipping order test data seeding.")
                return
            }

            // Group tables by restaurant to distribute orders across both restaurants
            val tablesByRestaurant = tables.groupBy { it.restaurantId }
            val restaurantIds = tablesByRestaurant.keys.toList()
            logger.info("Found ${restaurantIds.size} restaurants with tables: ${restaurantIds.joinToString()}")

        val orders = mutableListOf<OrderEntity>()
        val orderItems = mutableListOf<OrderItemEntity>()

        // Helper function to create order items for an order
        fun createOrderItemsForOrder(orderId: Int, orderCreatedAt: Instant, numItems: Int = Random.nextInt(2, 6)) {
            val selectedProducts = products.shuffled().take(numItems)
            selectedProducts.forEach { product ->
                val quantity = Random.nextInt(1, 4) // 1-3 items
                val price = product.price ?: BigDecimal.ZERO
                
                orderItems.add(
                    OrderItemEntity(
                        orderId = orderId,
                        productId = product.id,
                        quantity = quantity,
                        price = price,
                        status = "PENDING",
                        createdAt = orderCreatedAt,
                        updatedAt = orderCreatedAt
                    )
                )
            }
        }

        // Create current/recent orders (for testing active workflow):
        // - 2 READY orders
        // - 3 IN_PROGRESS orders
        // - 2 PENDING orders

        // 2 READY orders (today/recent) - prepared by kitchen chefs
        // Distribute across restaurants
        for (i in 1..2) {
            val restaurantId = restaurantIds[i % restaurantIds.size]
            val restaurantTables = tablesByRestaurant[restaurantId] ?: emptyList()
            if (restaurantTables.isEmpty()) continue
            
            val table = restaurantTables[i % restaurantTables.size]
            val createdAt = Instant.now().minusSeconds((i * 3600).toLong())
            val preparedBy = if (kitchenChefs.isNotEmpty()) kitchenChefs[i % kitchenChefs.size].id else null
            val waiter = if (waiters.isNotEmpty()) waiters[i % waiters.size].id else null
            val order = OrderEntity(
                tableId = table.id,
                restaurantId = restaurantId,
                staffId = waiter, // Waiter who took the order
                status = "READY",
                preparedByStaffId = preparedBy, // Kitchen chef who prepared it
                totalAmount = BigDecimal.ZERO,
                createdAt = createdAt,
                updatedAt = createdAt.plusSeconds(1800)
            )
            orders.add(order)
        }

        // 3 IN_PROGRESS orders (today/recent) - being prepared by kitchen chefs
        // Distribute across restaurants
        for (i in 1..3) {
            val restaurantId = restaurantIds[i % restaurantIds.size]
            val restaurantTables = tablesByRestaurant[restaurantId] ?: emptyList()
            if (restaurantTables.isEmpty()) continue
            
            val table = restaurantTables[(i + 2) % restaurantTables.size]
            val createdAt = Instant.now().minusSeconds((i * 2400).toLong())
            val preparedBy = if (kitchenChefs.isNotEmpty()) kitchenChefs[i % kitchenChefs.size].id else null
            val waiter = if (waiters.isNotEmpty()) waiters[(i + 1) % waiters.size].id else null
            val order = OrderEntity(
                tableId = table.id,
                restaurantId = restaurantId,
                staffId = waiter, // Waiter who took the order
                status = "IN_PROGRESS",
                claimedByStaffId = preparedBy, // Kitchen chef who claimed it
                preparedByStaffId = preparedBy, // Kitchen chef who is preparing it
                totalAmount = BigDecimal.ZERO,
                createdAt = createdAt,
                updatedAt = createdAt.plusSeconds(1200)
            )
            orders.add(order)
        }

        // 2 PENDING orders (today/recent) - just created by waiters
        // Distribute across restaurants
        for (i in 1..2) {
            val restaurantId = restaurantIds[i % restaurantIds.size]
            val restaurantTables = tablesByRestaurant[restaurantId] ?: emptyList()
            if (restaurantTables.isEmpty()) continue
            
            val table = restaurantTables[(i + 5) % restaurantTables.size]
            val createdAt = Instant.now().minusSeconds((i * 1800).toLong())
            val waiter = if (waiters.isNotEmpty()) waiters[(i + 3) % waiters.size].id else null
            val order = OrderEntity(
                tableId = table.id,
                restaurantId = restaurantId,
                staffId = waiter, // Waiter who took the order
                status = "PENDING",
                totalAmount = BigDecimal.ZERO,
                createdAt = createdAt,
                updatedAt = createdAt.plusSeconds(900)
            )
            orders.add(order)
        }

        // Create explicit older READY orders for testing payment page (demo: 3 orders)
        // These are guaranteed to exist so we can test the "older orders" functionality
        // Distribute across restaurants
        logger.info("Creating explicit older READY orders for testing payment page...")
        for (i in 1..3) {
            val daysAgo = i // 1, 2, 3 days ago
            val restaurantId = restaurantIds[i % restaurantIds.size]
            val restaurantTables = tablesByRestaurant[restaurantId] ?: emptyList()
            if (restaurantTables.isEmpty()) continue
            
            val table = restaurantTables[i % restaurantTables.size]
            val createdAt = Instant.now().minus(daysAgo.toLong(), ChronoUnit.DAYS)
                .minusSeconds((i * 3600).toLong()) // Spread throughout the day
            val updatedAt = createdAt.plusSeconds(Random.nextInt(1800, 7200).toLong()) // Made ready 30min-2h after creation
            val preparedBy = if (kitchenChefs.isNotEmpty()) kitchenChefs[i % kitchenChefs.size].id else null
            val waiter = if (waiters.isNotEmpty()) waiters[i % waiters.size].id else null
            
            val order = OrderEntity(
                tableId = table.id,
                restaurantId = restaurantId,
                staffId = waiter, // Waiter who took the order
                status = "READY",
                preparedByStaffId = preparedBy, // Kitchen chef who prepared it
                totalAmount = BigDecimal.ZERO,
                createdAt = createdAt,
                updatedAt = updatedAt,
                completedAt = null
            )
            orders.add(order)
        }
        logger.info("Created 3 explicit older READY orders (1 per day for last 3 days)")

        // Create historical COMPLETED orders for order history (last 14 days)
        // These provide a realistic order history without cluttering the payment page
        logger.info("Creating historical COMPLETED orders for order history...")
        var historicalOrdersCount = 0
        for (daysAgo in 1..14) {
            // Create 3-8 orders per day (realistic but not overwhelming)
            val ordersForDay = Random.nextInt(3, 9)
            
            for (orderIndex in 1..ordersForDay) {
                // Distribute orders across restaurants (alternate between restaurants)
                val restaurantId = restaurantIds[orderIndex % restaurantIds.size]
                val restaurantTables = tablesByRestaurant[restaurantId] ?: emptyList()
                if (restaurantTables.isEmpty()) continue
                
                val table = restaurantTables[Random.nextInt(restaurantTables.size)]
                val createdAt = Instant.now()
                    .minus(daysAgo.toLong(), ChronoUnit.DAYS)
                    .minusSeconds(Random.nextInt(0, 16 * 3600).toLong()) // Spread throughout the day
                
                // All historical orders are COMPLETED - assign realistic employees
                val waiter = if (waiters.isNotEmpty()) waiters[Random.nextInt(waiters.size)].id else null
                val preparedBy = if (kitchenChefs.isNotEmpty()) kitchenChefs[Random.nextInt(kitchenChefs.size)].id else null
                val completedAt = createdAt.plusSeconds(Random.nextInt(1800, 10800).toLong()) // Completed 30min-3h after creation
                val updatedAt = completedAt
                
                val order = OrderEntity(
                    tableId = table.id,
                    restaurantId = restaurantId,
                    staffId = waiter, // Waiter who took the order
                    status = "COMPLETED",
                    preparedByStaffId = preparedBy, // Kitchen chef who prepared it
                    totalAmount = BigDecimal.ZERO,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                    completedAt = completedAt
                )
                orders.add(order)
                historicalOrdersCount++
            }
        }
        logger.info("Created $historicalOrdersCount historical COMPLETED orders (last 14 days)")

        // Save all orders first
        logger.info("About to save ${orders.size} orders to database...")
        val savedOrders = orderR2dbcRepository.saveAll(orders).collectList().block() ?: emptyList()
        logger.info("Successfully saved ${savedOrders.size} orders to database")
        
        // Log distribution per day for verification
        try {
            val ordersByDay = savedOrders.groupBy { 
                it.createdAt.atZone(ZoneId.systemDefault()).toLocalDate() 
            }
            val topDays = ordersByDay.entries.sortedByDescending { it.value.size }.take(5)
            logger.info("Top 5 busiest days:")
            topDays.forEach { (date, dayOrders) ->
                logger.info("  ${date}: ${dayOrders.size} orders")
            }
        } catch (e: Exception) {
            logger.warn("Could not calculate day distribution: ${e.message}")
        }

        // Create order items for each order and assign staff based on order status
        // Each order item can be assigned to a different kitchen chef (realistic scenario)
        savedOrders.forEach { order ->
            val orderId = order.id
            if (orderId == null) return@forEach

            // Create 2-5 order items per order
            val numItems = Random.nextInt(2, 6)
            val selectedProducts = products.shuffled().take(numItems)
            
            // Determine if staff should be assigned based on order status
            val shouldAssignStaff = when (order.status) {
                "IN_PROGRESS", "READY", "COMPLETED" -> true
                else -> false // PENDING orders don't have assigned staff yet
            }
            
            selectedProducts.forEach { product ->
                val quantity = Random.nextInt(1, 4) // 1-3 items per product
                val price = product.price ?: BigDecimal.ZERO
                
                // Assign a different kitchen chef to each item (or null for PENDING)
                // This creates a realistic scenario where multiple chefs work on different items in the same order
                val assignedStaffId = if (shouldAssignStaff && kitchenChefs.isNotEmpty()) {
                    // Randomly assign a kitchen chef to this specific item
                    // Different items in the same order can have different chefs
                    kitchenChefs[Random.nextInt(kitchenChefs.size)].id
                } else {
                    null // PENDING orders don't have assigned staff yet
                }
                
                // Determine item status based on order status
                val itemStatus = when (order.status) {
                    "COMPLETED" -> "COMPLETED"
                    "READY" -> "READY"
                    "IN_PROGRESS" -> "IN_PROGRESS"
                    else -> "PENDING"
                }

                orderItems.add(
                    OrderItemEntity(
                        orderId = orderId,
                        productId = product.id,
                        staffId = assignedStaffId, // Kitchen chef assigned to prepare this specific item (can differ per item)
                        quantity = quantity,
                        price = price,
                        status = itemStatus,
                        createdAt = order.createdAt,
                        updatedAt = if (order.status != "PENDING") order.updatedAt else order.createdAt
                    )
                )
            }
        }

        // Save all order items
        val savedOrderItems = orderItemR2dbcRepository.saveAll(orderItems).collectList().block() ?: emptyList()
        logger.info("Created ${savedOrderItems.size} test order items")

        // Calculate and update total amounts for each order
        savedOrders.forEach { order ->
            val orderId = order.id
            if (orderId == null) return@forEach
            
            val itemsForOrder = savedOrderItems.filter { it.orderId == orderId }
            val totalAmount = itemsForOrder.sumOf { 
                (it.price ?: BigDecimal.ZERO) * BigDecimal(it.quantity) 
            }

            // For older READY orders, preserve the original updatedAt date
            // For other orders, it's OK to update updatedAt to now
            val shouldPreserveUpdatedAt = order.status == "READY" && 
                order.updatedAt.isBefore(Instant.now().minus(1, ChronoUnit.DAYS))

            val updatedOrder = order.copy(
                totalAmount = totalAmount,
                updatedAt = if (shouldPreserveUpdatedAt) order.updatedAt else Instant.now()
            )
            orderR2dbcRepository.save(updatedOrder).block()
        }

        // Count orders by status and by day for summary
        val statusCounts = savedOrders.groupingBy { it.status }.eachCount()
        val todayOrders = savedOrders.filter { 
            it.createdAt.isAfter(Instant.now().minus(1, ChronoUnit.DAYS)) 
        }
        val todayStatusCounts = todayOrders.groupingBy { it.status }.eachCount()
        
        val olderReadyOrders = savedOrders.filter { 
            it.status == "READY" && it.createdAt.isBefore(Instant.now().minus(1, ChronoUnit.DAYS))
        }
        val historicalCompletedOrders = savedOrders.filter { 
            it.status == "COMPLETED" && it.createdAt.isBefore(Instant.now().minus(1, ChronoUnit.DAYS))
        }
        
        logger.info("=".repeat(60))
        logger.info("Order test data seeding completed!")
        logger.info("Summary: ${savedOrders.size} total orders created")
        logger.info("  - Orders from today: ${todayOrders.size} (${todayStatusCounts.entries.joinToString(", ") { "${it.key}: ${it.value}" }})")
        logger.info("  - Older READY orders (for demo): ${olderReadyOrders.size} orders")
        logger.info("  - Historical COMPLETED orders (for history): ${historicalCompletedOrders.size} orders from last 14 days")
        logger.info("  - Total status distribution: ${statusCounts.entries.joinToString(", ") { "${it.key}: ${it.value}" }}")
        logger.info("  - Total order items: ${savedOrderItems.size}")
        logger.info("=".repeat(60))
        
            // Verify orders were actually saved
            val verifyOrders = orderR2dbcRepository.findAll().collectList().block() ?: emptyList()
            logger.info("Verification: Found ${verifyOrders.size} orders in database after seeding")
            
        } catch (e: Exception) {
            logger.error("ERROR in OrderTestDataSeeder: ${e.message}", e)
            e.printStackTrace()
            throw e
        }
    }
}

