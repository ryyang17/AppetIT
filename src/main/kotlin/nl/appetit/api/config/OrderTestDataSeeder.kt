package nl.appetit.api.config

import nl.appetit.api.data.entity.OrderEntity
import nl.appetit.api.data.entity.OrderItemEntity
import nl.appetit.api.data.repository.OrderR2dbcRepository
import nl.appetit.api.data.repository.OrderItemR2dbcRepository
import nl.appetit.api.data.repository.ProductR2dbcRepository
import nl.appetit.api.data.repository.TableR2dbcRepository
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
    private val tableR2dbcRepository: TableR2dbcRepository
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

            // Get available products and tables
            logger.info("Fetching products and tables...")
            val products = productR2dbcRepository.findAll().collectList().block() ?: emptyList()
            val tables = tableR2dbcRepository.findAll().collectList().block() ?: emptyList()

            logger.info("Found ${products.size} products and ${tables.size} tables")

            if (products.isEmpty()) {
                logger.error("No products found. Skipping order test data seeding.")
                return
            }

            if (tables.isEmpty()) {
                logger.error("No tables found. Skipping order test data seeding.")
                return
            }

            // Get first restaurant ID from tables
            val restaurantId = tables.firstOrNull()?.restaurantId

        val orders = mutableListOf<OrderEntity>()
        val orderItems = mutableListOf<OrderItemEntity>()

        // Helper function to create a random timestamp in the past
        fun randomPastTimestamp(daysAgo: Int, hoursVariation: Int = 12): Instant {
            val baseTime = Instant.now().minus(daysAgo.toLong(), ChronoUnit.DAYS)
            val randomHours = Random.nextInt(-hoursVariation, hoursVariation + 1)
            val randomMinutes = Random.nextInt(0, 60)
            return baseTime
                .plus(randomHours.toLong(), ChronoUnit.HOURS)
                .plus(randomMinutes.toLong(), ChronoUnit.MINUTES)
        }

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

        // 2 READY orders (today/recent)
        for (i in 1..2) {
            val table = tables[i % tables.size]
            val createdAt = Instant.now().minusSeconds((i * 3600).toLong())
            val order = OrderEntity(
                tableId = table.id,
                restaurantId = restaurantId,
                status = "READY",
                totalAmount = BigDecimal.ZERO,
                createdAt = createdAt,
                updatedAt = createdAt.plusSeconds(1800)
            )
            orders.add(order)
        }

        // 3 IN_PROGRESS orders (today/recent)
        for (i in 1..3) {
            val table = tables[(i + 2) % tables.size]
            val createdAt = Instant.now().minusSeconds((i * 2400).toLong())
            val order = OrderEntity(
                tableId = table.id,
                restaurantId = restaurantId,
                status = "IN_PROGRESS",
                totalAmount = BigDecimal.ZERO,
                createdAt = createdAt,
                updatedAt = createdAt.plusSeconds(1200)
            )
            orders.add(order)
        }

        // 2 PENDING orders (today/recent)
        for (i in 1..2) {
            val table = tables[(i + 5) % tables.size]
            val createdAt = Instant.now().minusSeconds((i * 1800).toLong())
            val order = OrderEntity(
                tableId = table.id,
                restaurantId = restaurantId,
                status = "PENDING",
                totalAmount = BigDecimal.ZERO,
                createdAt = createdAt,
                updatedAt = createdAt.plusSeconds(900)
            )
            orders.add(order)
        }

        // Create historical orders (last 60 days)
        // Some days will be very busy (20-100 orders), others quiet (1-5 orders)
        // This creates a realistic distribution
        
        // Generate orders for each of the last 60 days
        for (daysAgo in 0..60) {
            // Determine how many orders for this day
            // Some days are very busy, others quiet
            val ordersForDay = when {
                daysAgo == 0 -> Random.nextInt(15, 25) // Today: 15-25 orders (mix of statuses)
                daysAgo == 1 -> Random.nextInt(20, 35) // Yesterday: busy
                daysAgo == 2 -> Random.nextInt(10, 20) // Day before: moderate
                daysAgo <= 7 -> {
                    // Last week: variable (some busy days, some quiet)
                    when (Random.nextInt(100)) {
                        in 0..10 -> Random.nextInt(50, 100)  // 10% chance: very busy day (50-100 orders)
                        in 11..30 -> Random.nextInt(20, 50)  // 20% chance: busy day (20-50 orders)
                        in 31..60 -> Random.nextInt(10, 20)   // 30% chance: moderate day (10-20 orders)
                        else -> Random.nextInt(1, 10)        // 40% chance: quiet day (1-10 orders)
                    }
                }
                daysAgo <= 14 -> {
                    // Week 2: mostly moderate with occasional busy days
                    when (Random.nextInt(100)) {
                        in 0..5 -> Random.nextInt(30, 60)    // 5% chance: busy day
                        in 6..30 -> Random.nextInt(10, 25)  // 25% chance: moderate day
                        else -> Random.nextInt(1, 10)         // 70% chance: quiet day
                    }
                }
                daysAgo <= 30 -> {
                    // Week 3-4: mostly quiet with occasional busy days
                    when (Random.nextInt(100)) {
                        in 0..3 -> Random.nextInt(20, 40)    // 3% chance: busy day
                        in 4..20 -> Random.nextInt(5, 15)    // 17% chance: moderate day
                        else -> Random.nextInt(1, 8)          // 80% chance: quiet day
                    }
                }
                else -> {
                    // Older than 30 days: mostly quiet
                    when (Random.nextInt(100)) {
                        in 0..2 -> Random.nextInt(10, 25)     // 2% chance: moderate day
                        else -> Random.nextInt(1, 5)          // 98% chance: quiet day (1-5 orders)
                    }
                }
            }

            // Create orders for this day
            // IMPORTANT: Each order is a separate order, even if from the same table
            // Multiple orders from the same table on the same day are all counted separately
            for (orderIndex in 1..ordersForDay) {
                // Spread orders throughout the day (different times)
                val hoursVariation = 16 // Orders between 8:00 and 24:00
                val createdAt = if (daysAgo == 0) {
                    // Today: spread throughout today (some recent, some earlier today)
                    Instant.now().minusSeconds(Random.nextInt(0, 16 * 3600).toLong())
                } else {
                    randomPastTimestamp(daysAgo, hoursVariation)
                }
                
                // Randomly assign to any table - same table can have multiple orders per day
                // This is realistic: a table can order multiple times (appetizer, main course, dessert, drinks, etc.)
                val table = tables[Random.nextInt(tables.size)]
                
                // Status distribution based on how old the order is
                // IMPORTANT: Orders from previous days (daysAgo > 0) should be COMPLETED
                // Only today's orders can have active statuses (PENDING, IN_PROGRESS, READY)
                val status = when {
                    daysAgo == 0 -> {
                        // Today: mix of all statuses (more active orders)
                        when (Random.nextInt(100)) {
                            in 0..30 -> "PENDING"        // 30% pending
                            in 31..60 -> "IN_PROGRESS"   // 30% in progress
                            in 61..85 -> "READY"         // 25% ready
                            else -> "COMPLETED"          // 15% completed (from earlier today)
                        }
                    }
                    else -> {
                        // All previous days: ALL orders should be COMPLETED
                        // This makes sense - orders from yesterday or older should be finished
                        "COMPLETED"
                    }
                }

                // For completed orders, set completedAt
                val completedAt = if (status == "COMPLETED") {
                    createdAt.plusSeconds(Random.nextInt(1800, 7200).toLong()) // 30min - 2 hours later
                } else null

                // UpdatedAt: for active orders, might be updated recently
                val updatedAt = when {
                    status == "COMPLETED" -> completedAt ?: createdAt.plusSeconds(Random.nextInt(1800, 7200).toLong())
                    daysAgo == 0 && status != "COMPLETED" -> {
                        // Today's active orders: might be updated recently
                        if (Random.nextBoolean()) {
                            Instant.now().minusSeconds(Random.nextInt(0, 3600).toLong()) // Updated in last hour
                        } else {
                            createdAt.plusSeconds(Random.nextInt(300, 3600).toLong())
                        }
                    }
                    else -> createdAt.plusSeconds(Random.nextInt(300, 3600).toLong())
                }

                val order = OrderEntity(
                    tableId = table.id,
                    restaurantId = restaurantId,
                    status = status,
                    totalAmount = BigDecimal.ZERO,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                    completedAt = completedAt
                )
                orders.add(order)
            }
        }

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

        // Create order items for each order
        savedOrders.forEach { order ->
            val orderId = order.id
            if (orderId == null) return@forEach

            // Create 2-5 order items per order
            val numItems = Random.nextInt(2, 6)
            val selectedProducts = products.shuffled().take(numItems)
            
            selectedProducts.forEach { product ->
                val quantity = Random.nextInt(1, 4) // 1-3 items per product
                val price = product.price ?: BigDecimal.ZERO

                orderItems.add(
                    OrderItemEntity(
                        orderId = orderId,
                        productId = product.id,
                        quantity = quantity,
                        price = price,
                        status = "PENDING",
                        createdAt = order.createdAt,
                        updatedAt = order.createdAt
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

            val updatedOrder = order.copy(
                totalAmount = totalAmount,
                updatedAt = Instant.now()
            )
            orderR2dbcRepository.save(updatedOrder).block()
        }

        // Count orders by status and by day for summary
        val statusCounts = savedOrders.groupingBy { it.status }.eachCount()
        val todayOrders = savedOrders.filter { 
            it.createdAt.isAfter(Instant.now().minus(1, ChronoUnit.DAYS)) 
        }
        val todayStatusCounts = todayOrders.groupingBy { it.status }.eachCount()
        
        logger.info("=".repeat(60))
        logger.info("Order test data seeding completed!")
        logger.info("Summary: ${savedOrders.size} total orders created")
        logger.info("  - Orders from today: ${todayOrders.size} (${todayStatusCounts.entries.joinToString(", ") { "${it.key}: ${it.value}" }})")
        logger.info("  - Historical orders: ${savedOrders.size - todayOrders.size} orders from last 60 days")
        logger.info("  - Total status distribution: ${statusCounts.entries.joinToString(", ") { "${it.key}: ${it.value}" }}")
        logger.info("  - Total order items: ${savedOrderItems.size}")
        logger.info("  - Orders distributed across 60 days with realistic busy/quiet day patterns")
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

