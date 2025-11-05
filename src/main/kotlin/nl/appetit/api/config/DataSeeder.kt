package nl.appetit.api.config

import nl.appetit.api.data.entity.CategoryEntity
import nl.appetit.api.data.entity.ProductEntity
import nl.appetit.api.data.repository.CategoryR2dbcRepository
import nl.appetit.api.data.repository.ProductR2dbcRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal
@profile("!prod")
@Component
class DataSeeder(
    private val categoryR2dbcRepository: CategoryR2dbcRepository,
    private val productR2dbcRepository: ProductR2dbcRepository
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(DataSeeder::class.java)

    override fun run(vararg args: String) {
        logger.info("Starting data seeding...")
        
        // Delete all existing data
        logger.info("Deleting all existing data...")
        productRepository.deleteAll().block()
        categoryR2dbcRepository.deleteAll().block()
        logger.info("Data deleted successfully")
        
        // Seed categories with hierarchy
        val categoryMap = seedCategories()
        
        // Seed products with category assignments
        seedProducts(categoryMap)
        
        logger.info("Data seeding completed!")
    }

    private fun seedCategories(): Map<String, Long> {
        logger.info("Seeding categories with hierarchy...")
        
        val savedCategories = mutableMapOf<String, Long>()
        
        // Root categories
        val food = categoryR2dbcRepository.save(CategoryEntity(name = "Food", order = 1)).block()!!
        savedCategories["Food"] = food.id!!
        
        val beverages = categoryR2dbcRepository.save(CategoryEntity(name = "Beverages", order = 2)).block()!!
        savedCategories["Beverages"] = beverages.id!!
        
        // Food subcategories
        val appetizers = categoryR2dbcRepository.save(
            CategoryEntity(name = "Appetizers", parentId = food.id!!, order = 1)
        ).block()!!
        savedCategories["Appetizers"] = appetizers.id!!
        
        val mainCourses = categoryR2dbcRepository.save(
            CategoryEntity(name = "Main Courses", parentId = food.id!!, order = 2)
        ).block()!!
        savedCategories["Main Courses"] = mainCourses.id!!
        
        val salads = categoryR2dbcRepository.save(
            CategoryEntity(name = "Salads", parentId = food.id!!, order = 3)
        ).block()!!
        savedCategories["Salads"] = salads.id!!
        
        val desserts = categoryR2dbcRepository.save(
            CategoryEntity(name = "Desserts", parentId = food.id!!, order = 4)
        ).block()!!
        savedCategories["Desserts"] = desserts.id!!
        
        // Appetizers sub-subcategories
        val breadStarters = categoryR2dbcRepository.save(
            CategoryEntity(name = "Bread & Starters", parentId = appetizers.id!!, order = 1)
        ).block()!!
        savedCategories["Bread & Starters"] = breadStarters.id!!
        
        val smallPlates = categoryR2dbcRepository.save(
            CategoryEntity(name = "Small Plates", parentId = appetizers.id!!, order = 2)
        ).block()!!
        savedCategories["Small Plates"] = smallPlates.id!!
        
        // Main Courses sub-subcategories
        val meatDishes = categoryR2dbcRepository.save(
            CategoryEntity(name = "Meat Dishes", parentId = mainCourses.id!!, order = 1)
        ).block()!!
        savedCategories["Meat Dishes"] = meatDishes.id!!
        
        val seafood = categoryR2dbcRepository.save(
            CategoryEntity(name = "Seafood", parentId = mainCourses.id!!, order = 2)
        ).block()!!
        savedCategories["Seafood"] = seafood.id!!
        
        val pasta = categoryR2dbcRepository.save(
            CategoryEntity(name = "Pasta", parentId = mainCourses.id!!, order = 3)
        ).block()!!
        savedCategories["Pasta"] = pasta.id!!
        
        val vegetarian = categoryR2dbcRepository.save(
            CategoryEntity(name = "Vegetarian", parentId = mainCourses.id!!, order = 4)
        ).block()!!
        savedCategories["Vegetarian"] = vegetarian.id!!
        
        // Beverages subcategories
        val hotDrinks = categoryR2dbcRepository.save(
            CategoryEntity(name = "Hot Drinks", parentId = beverages.id!!, order = 1)
        ).block()!!
        savedCategories["Hot Drinks"] = hotDrinks.id!!
        
        val coldDrinks = categoryR2dbcRepository.save(
            CategoryEntity(name = "Cold Drinks", parentId = beverages.id!!, order = 2)
        ).block()!!
        savedCategories["Cold Drinks"] = coldDrinks.id!!
        
        val alcoholic = categoryR2dbcRepository.save(
            CategoryEntity(name = "Alcoholic", parentId = beverages.id!!, order = 3)
        ).block()!!
        savedCategories["Alcoholic"] = alcoholic.id!!



        // Desserts subcategories
        val cakes = categoryR2dbcRepository.save(
            CategoryEntity(name = "Cakes", parentId = desserts.id!!, order = 1)
        ).block()!!
        savedCategories["Cakes"] = cakes.id!!

        val iceCreams = categoryR2dbcRepository.save(
            CategoryEntity(name = "Ice Creams", parentId = desserts.id!!, order = 2)
        ).block()!!
        savedCategories["Ice Creams"] = iceCreams.id!!

        val pastries = categoryR2dbcRepository.save(
            CategoryEntity(name = "Pastries", parentId = desserts.id!!, order = 3)
        ).block()!!
        savedCategories["Pastries"] = pastries.id!!

        // Salads subcategories
        val greenSalads = categoryR2dbcRepository.save(
            CategoryEntity(name = "Green Salads", parentId = salads.id!!, order = 1)
        ).block()!!
        savedCategories["Green Salads"] = greenSalads.id!!

        val fruitSalads = categoryR2dbcRepository.save(
            CategoryEntity(name = "Fruit Salads", parentId = salads.id!!, order = 2)
        ).block()!!
        savedCategories["Fruit Salads"] = fruitSalads.id!!

        val proteinSalads = categoryR2dbcRepository.save(
            CategoryEntity(name = "Protein Salads", parentId = salads.id!!, order = 3)
        ).block()!!
        savedCategories["Protein Salads"] = proteinSalads.id!!
        
        logger.info("${savedCategories.size} categories seeded successfully")
        return savedCategories


    }

    private fun seedProducts(categoryMap: Map<String, Long>) {
        logger.info("Seeding products...")
        
        val products = listOf(
            // Bread & Starters
            ProductEntity(
                name = "Garlic Bread",
                price = BigDecimal("4.50"),
                description = "Fresh baked bread with garlic butter and herbs",
                imageUrl = "https://images.unsplash.com/photo-1751199592465-f142293a8cc6?q=80&w=1168&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Bread & Starters"]?.toInt()
            ),
            ProductEntity(
                name = "Bruschetta",
                price = BigDecimal("6.00"),
                description = "Toasted bread topped with tomatoes, basil, and mozzarella",
                imageUrl = "https://images.unsplash.com/photo-1748718826530-06b08d46d078?q=80&w=724&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Bread & Starters"]?.toInt()
            ),
            
            // Small Plates
            ProductEntity(
                name = "Chicken Wings",
                price = BigDecimal("8.50"),
                description = "Crispy chicken wings with your choice of sauce",
                imageUrl = "https://images.unsplash.com/photo-1567620832903-9fc6debc209f?q=80&w=960&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Small Plates"]?.toInt()
            ),
            
            // Meat Dishes
            ProductEntity(
                name = "Beef Steak",
                price = BigDecimal("24.00"),
                description = "Premium ribeye steak cooked to your preference",
                imageUrl = "https://plus.unsplash.com/premium_photo-1723478557023-1f739ec06671?q=80&w=1672&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Meat Dishes"]?.toInt()
            ),
            ProductEntity(
                name = "Chicken Parmesan",
                price = BigDecimal("16.50"),
                description = "Breaded chicken breast with marinara sauce and mozzarella",
                imageUrl = "https://images.unsplash.com/photo-1632778149955-e80f8ceca2e8?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Meat Dishes"]?.toInt()
            ),
            
            // Seafood
            ProductEntity(
                name = "Grilled Salmon",
                price = BigDecimal("18.50"),
                description = "Fresh Atlantic salmon grilled to perfection with lemon butter",
                imageUrl = "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Seafood"]?.toInt()
            ),
            ProductEntity(
                name = "Shrimp Scampi",
                price = BigDecimal("17.00"),
                description = "Tender shrimp in white wine and garlic sauce",
                imageUrl = "https://images.unsplash.com/photo-1598103442097-8b74394b95c6?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Seafood"]?.toInt()
            ),
            
            // Pasta
            ProductEntity(
                name = "Vegetarian Pasta",
                price = BigDecimal("14.00"),
                description = "Penne pasta with seasonal vegetables in a light cream sauce",
                imageUrl = "https://media.istockphoto.com/id/1189709277/nl/foto/pasta-penne-met-geroosterde-tomaat-saus-mozzarella-kaas-grijze-stenen-achtergrond-bovenaanzicht.jpg?s=1024x1024&w=is&k=20&c=xdBy9QAifujU1gYAI0HSMQWzxuLKj4xfU3bqUhNNR4k=",
                isAvailable = true,
                categoryId = categoryMap["Pasta"]?.toInt()
            ),
            ProductEntity(
                name = "Spaghetti Bolognese",
                price = BigDecimal("13.00"),
                description = "Classic spaghetti with traditional meat sauce",
                imageUrl = "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Pasta"]?.toInt()
            ),
            
            // Vegetarian
            ProductEntity(
                name = "Veggie Burger",
                price = BigDecimal("12.50"),
                description = "House-made veggie patty with fresh toppings",
                imageUrl = "https://images.unsplash.com/photo-1585238341710-4dd9e42e1e9a?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Vegetarian"]?.toInt()
            ),
            
            // Salads
            ProductEntity(
                name = "Caesar Salad",
                price = BigDecimal("9.50"),
                description = "Fresh romaine lettuce with Caesar dressing and croutons",
                imageUrl = "https://images.unsplash.com/photo-1550304943-4f24f54ddde9?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Salads"]?.toInt()
            ),
            ProductEntity(
                name = "Greek Salad",
                price = BigDecimal("10.50"),
                description = "Mixed greens with feta cheese, olives, and Greek dressing",
                imageUrl = "https://plus.unsplash.com/premium_photo-1676047258557-de72954cf17c?q=80&w=878&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Salads"]?.toInt()
            ),
            ProductEntity(
                name = "Caprese Salad",
                price = BigDecimal("11.00"),
                description = "Fresh mozzarella, tomatoes, basil with balsamic glaze",
                imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Salads"]?.toInt()
            ),
            
            // Desserts
            ProductEntity(
                name = "Chocolate Cake",
                price = BigDecimal("6.50"),
                description = "Rich chocolate cake with chocolate ganache",
                imageUrl = "https://images.unsplash.com/photo-1597083722160-c31d67d4af44?q=80&w=1816&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Desserts"]?.toInt()
            ),
            ProductEntity(
                name = "Tiramisu",
                price = BigDecimal("7.00"),
                description = "Classic Italian dessert with coffee and mascarpone",
                imageUrl = "https://plus.unsplash.com/premium_photo-1695028378225-97fbe39df62a?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Desserts"]?.toInt()
            ),
            ProductEntity(
                name = "Ice Cream Sundae",
                price = BigDecimal("5.50"),
                description = "Vanilla ice cream with chocolate sauce and whipped cream",
                imageUrl = "https://images.unsplash.com/photo-1657225953401-5f95007fc8e0?q=80&w=1738&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Desserts"]?.toInt()
            ),
            
            // Hot Drinks
            ProductEntity(
                name = "Coffee",
                price = BigDecimal("2.50"),
                description = "Freshly brewed espresso-based coffee",
                imageUrl = "https://images.unsplash.com/photo-1495774856032-8b90bbb32b32?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Hot Drinks"]?.toInt()
            ),
            ProductEntity(
                name = "Cappuccino",
                price = BigDecimal("3.50"),
                description = "Espresso with steamed milk and foam",
                imageUrl = "https://images.unsplash.com/photo-1517668808822-9ebb02ae2a0e?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Hot Drinks"]?.toInt()
            ),
            ProductEntity(
                name = "Hot Chocolate",
                price = BigDecimal("3.00"),
                description = "Creamy hot chocolate with whipped cream",
                imageUrl = "https://images.unsplash.com/photo-1578985545062-69928b1d9587?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Hot Drinks"]?.toInt()
            ),
            
            // Cold Drinks
            ProductEntity(
                name = "Fresh Orange Juice",
                price = BigDecimal("3.50"),
                description = "Freshly squeezed orange juice",
                imageUrl = "https://images.unsplash.com/photo-1607690506833-498e04ab3ffa?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Cold Drinks"]?.toInt()
            ),
            ProductEntity(
                name = "Sparkling Water",
                price = BigDecimal("2.00"),
                description = "Refreshing sparkling water",
                imageUrl = "https://images.unsplash.com/photo-1619622683368-8a66b4b5c420?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Cold Drinks"]?.toInt()
            ),
            ProductEntity(
                name = "Iced Tea",
                price = BigDecimal("2.75"),
                description = "Chilled iced tea with fresh lemon",
                imageUrl = "https://images.unsplash.com/photo-1570020176750-e0dd52f6a83d?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Cold Drinks"]?.toInt()
            ),
            
            // Alcoholic
            ProductEntity(
                name = "House Red Wine",
                price = BigDecimal("5.50"),
                description = "Selection of premium red wine by the glass",
                imageUrl = "https://images.unsplash.com/photo-1510812431401-41d2cab2707d?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Alcoholic"]?.toInt()
            ),
            ProductEntity(
                name = "House White Wine",
                price = BigDecimal("5.50"),
                description = "Selection of premium white wine by the glass",
                imageUrl = "https://images.unsplash.com/photo-1510812431401-41d2cab2707d?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Alcoholic"]?.toInt()
            ),
            ProductEntity(
                name = "Craft Beer",
                price = BigDecimal("4.50"),
                description = "Selection of local craft beers",
                imageUrl = "https://images.unsplash.com/photo-1608270861620-7c40f36e1b5d?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                isAvailable = true,
                categoryId = categoryMap["Alcoholic"]?.toInt()
            )
        )
        
        productR2dbcRepository.saveAll(products).collectList().block()
        logger.info("${products.size} products seeded successfully")
    }
}