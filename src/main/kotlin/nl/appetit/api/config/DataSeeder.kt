package nl.appetit.api.config

import nl.appetit.api.model.Category
import nl.appetit.api.model.Product
import nl.appetit.api.repository.CategoryRepository
import nl.appetit.api.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class DataSeeder(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(DataSeeder::class.java)

    override fun run(vararg args: String) {
        logger.info("Starting data seeding...")
        
        // Delete all existing categories
        logger.info("Deleting all existing categories...")
        categoryRepository.deleteAll().block()
        logger.info("Categories deleted successfully")
        
        // Check if products exist
        if (productRepository.count().block() == 0L) {
            seedProducts()
        } else {
            logger.info("Products already exist, skipping product seeding")
        }
        
        logger.info("Data seeding completed!")
    }


    private fun seedProducts() {
        logger.info("Seeding products...")
        
        // Set all products to have categoryId = null (no category)
        logger.info("Setting all products to categoryId = null (no category)")
        
        val products = listOf(
            // Appetizers
            Product(
                name = "Garlic Bread",
                price = BigDecimal("4.50"),
                description = "Fresh baked bread with garlic butter and herbs",
                imageUrl = "https://images.unsplash.com/photo-1751199592465-f142293a8cc6?q=80&w=1168&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = null
            ),
            Product(
                name = "Bruschetta",
                price = BigDecimal("6.00"),
                description = "Toasted bread topped with tomatoes, basil, and mozzarella",
                imageUrl = "https://images.unsplash.com/photo-1748718826530-06b08d46d078?q=80&w=724&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = null
            ),
            Product(
                name = "Chicken Wings",
                price = BigDecimal("8.50"),
                description = "Crispy chicken wings with your choice of sauce",
                imageUrl = "https://images.unsplash.com/photo-1567620832903-9fc6debc209f?q=80&w=960&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = null
            ),
            
            // Main Courses
            Product(
                name = "Grilled Salmon",
                price = BigDecimal("18.50"),
                description = "Fresh Atlantic salmon grilled to perfection with lemon butter",
                imageUrl = "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = null
            ),
            Product(
                name = "Beef Steak",
                price = BigDecimal("24.00"),
                description = "Premium ribeye steak cooked to your preference",
                imageUrl = "https://plus.unsplash.com/premium_photo-1723478557023-1f739ec06671?q=80&w=1672&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = null
            ),
            Product(
                name = "Chicken Parmesan",
                price = BigDecimal("16.50"),
                description = "Breaded chicken breast with marinara sauce and mozzarella",
                imageUrl = "https://images.unsplash.com/photo-1632778149955-e80f8ceca2e8?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = null
            ),
            Product(
                name = "Vegetarian Pasta",
                price = BigDecimal("14.00"),
                description = "Penne pasta with seasonal vegetables in a light cream sauce",
                imageUrl = "https://media.istockphoto.com/id/1189709277/nl/foto/pasta-penne-met-geroosterde-tomaat-saus-mozzarella-kaas-grijze-stenen-achtergrond-bovenaanzicht.jpg?s=1024x1024&w=is&k=20&c=xdBy9QAifujU1gYAI0HSMQWzxuLKj4xfU3bqUhNNR4k=",
                isAvailable = true,
                categoryId = null
            ),
            
            // Salads
            Product(
                name = "Caesar Salad",
                price = BigDecimal("9.50"),
                description = "Fresh romaine lettuce with Caesar dressing and croutons",
                imageUrl = "https://images.unsplash.com/photo-1550304943-4f24f54ddde9?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = null
            ),
            Product(
                name = "Greek Salad",
                price = BigDecimal("10.50"),
                description = "Mixed greens with feta cheese, olives, and Greek dressing",
                imageUrl = "https://plus.unsplash.com/premium_photo-1676047258557-de72954cf17c?q=80&w=878&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = null
            ),
            
            // Desserts
            Product(
                name = "Chocolate Cake",
                price = BigDecimal("6.50"),
                description = "Rich chocolate cake with chocolate ganache",
                imageUrl = "https://images.unsplash.com/photo-1597083722160-c31d67d4af44?q=80&w=1816&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = null
            ),
            Product(
                name = "Tiramisu",
                price = BigDecimal("7.00"),
                description = "Classic Italian dessert with coffee and mascarpone",
                imageUrl = "https://plus.unsplash.com/premium_photo-1695028378225-97fbe39df62a?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = null
            ),
            Product(
                name = "Ice Cream Sundae",
                price = BigDecimal("5.50"),
                description = "Vanilla ice cream with chocolate sauce and whipped cream",
                imageUrl = "https://images.unsplash.com/photo-1657225953401-5f95007fc8e0?q=80&w=1738&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = null
            ),
            
            // Beverages
            Product(
                name = "Fresh Orange Juice",
                price = BigDecimal("3.50"),
                description = "Freshly squeezed orange juice",
                imageUrl = "https://images.unsplash.com/photo-1607690506833-498e04ab3ffa?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = null
            ),
            Product(
                name = "Coffee",
                price = BigDecimal("2.50"),
                description = "Freshly brewed coffee",
                imageUrl = "https://images.unsplash.com/photo-1495774856032-8b90bbb32b32?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = null
            ),
            Product(
                name = "Sparkling Water",
                price = BigDecimal("2.00"),
                description = "Refreshing sparkling water",
                imageUrl = "https://images.unsplash.com/photo-1619622683368-8a66b4b5c420?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = null
            )
        )
        
        productRepository.saveAll(products).collectList().block()
        logger.info("Products seeded successfully")
    }
}