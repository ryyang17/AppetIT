package nl.appetit.api.config

import nl.appetit.api.data.entity.CategoryEntity
import nl.appetit.api.data.entity.ProductEntity
import nl.appetit.api.data.entity.TagEntity
import nl.appetit.api.data.entity.TranslationEntity
import nl.appetit.api.data.entity.TableEntity
import nl.appetit.api.data.entity.RestaurantEntity
import nl.appetit.api.data.entity.EmployeeEntity
import nl.appetit.api.data.entity.RestaurantProductEntity
import nl.appetit.api.data.repository.CategoryR2dbcRepository
import nl.appetit.api.data.repository.ProductR2dbcRepository
import nl.appetit.api.data.repository.TagR2dbcRepository
import nl.appetit.api.data.repository.ProductTagR2dbcRepository
import nl.appetit.api.data.repository.TranslationR2dbcRepository
import nl.appetit.api.data.repository.RestaurantR2dbcRepository
import nl.appetit.api.data.repository.TableR2dbcRepository
import nl.appetit.api.data.repository.OrderR2dbcRepository
import nl.appetit.api.data.repository.OrderItemR2dbcRepository
import nl.appetit.api.data.repository.EmployeeR2dbcRepository
import nl.appetit.api.data.repository.TablePaymentOrderR2dbcRepository
import nl.appetit.api.data.repository.TablePaymentR2dbcRepository
import nl.appetit.api.data.repository.RestaurantProductR2dbcRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.r2dbc.core.DatabaseClient
import java.math.BigDecimal

@Profile("!prod")
@Order(1) // Run first, before OrderTestDataSeeder
@Component
class DataSeeder(
    private val categoryR2dbcRepository: CategoryR2dbcRepository,
    private val productR2dbcRepository: ProductR2dbcRepository,
    private val tagR2dbcRepository: TagR2dbcRepository,
    private val productTagR2dbcRepository: ProductTagR2dbcRepository,
    private val translationR2dbcRepository: TranslationR2dbcRepository,
    private val restaurantR2dbcRepository: RestaurantR2dbcRepository,
    private val tableR2dbcRepository: TableR2dbcRepository,
    private val orderR2dbcRepository: OrderR2dbcRepository,
    private val orderItemR2dbcRepository: OrderItemR2dbcRepository,
    private val employeeR2dbcRepository: EmployeeR2dbcRepository,
    private val tablePaymentOrderR2dbcRepository: TablePaymentOrderR2dbcRepository,
    private val tablePaymentR2dbcRepository: TablePaymentR2dbcRepository,
    private val restaurantProductR2dbcRepository: RestaurantProductR2dbcRepository,
    private val databaseClient: DatabaseClient
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(DataSeeder::class.java)

    init {
        logger.info("DataSeeder component initialized and ready to run")
    }

    override fun run(vararg args: String) {
        logger.info("=".repeat(60))
        logger.info("DataSeeder.run() called - Starting data seeding...")
        logger.info("=".repeat(60))
        
        // Delete all existing data in correct order (respecting foreign key constraints)
        logger.info("Deleting all existing data...")
        // First delete dependent data (table payments, orders, order items)
        try {
            tablePaymentOrderR2dbcRepository.deleteAll().block()
            tablePaymentR2dbcRepository.deleteAll().block()
        } catch (e: Exception) {
            logger.warn("Failed to delete table payment data (this is OK on first run before migration): {}", e.message)
        }
        // Then delete orders and order items
        orderItemR2dbcRepository.deleteAll().block()
        orderR2dbcRepository.deleteAll().block()
        // Delete products (CASCADE will automatically delete product_tag records)
        productR2dbcRepository.deleteAll().block()
        categoryR2dbcRepository.deleteAll().block()
        tagR2dbcRepository.deleteAll().block()
        translationR2dbcRepository.deleteAll().block()
        // Delete tables
        tableR2dbcRepository.deleteAll().block()
        // Delete restaurant-product relationships
        try {
            restaurantProductR2dbcRepository.deleteAll().block()
        } catch (e: Exception) {
            logger.warn("Failed to delete restaurant-product relationships (this is OK on first run before migration): {}", e.message)
        }
        // Delete employees (will be recreated by seedEmployees)
        try {
            employeeR2dbcRepository.deleteAll().block()
        } catch (e: Exception) {
            logger.warn("Failed to delete employees (this is OK on first run before migration): {}", e.message)
        }
        // Also delete restaurants to ensure clean state (will be recreated if needed)
        restaurantR2dbcRepository.deleteAll().block()
        
        // Reset all sequences to start from 1
        // PostgreSQL auto-generates sequence names as: {table}_{column}_seq
        logger.info("Resetting sequences to start from 1...")
        try {
            resetSequence("order_item_order_item_id_seq", 1).block()
            resetSequence("order_order_id_seq", 1).block()
            resetSequence("product_product_id_seq", 1).block()
            resetSequence("category_category_id_seq", 1).block()
            resetSequence("tag_tag_id_seq", 1).block()
            resetSequence("translations_translation_id_seq", 1).block()
            resetSequence("table_table_id_seq", 1).block()
            resetSequence("restaurant_restaurant_id_seq", 1).block()
            // New payment-related tables
            resetSequence("table_payment_table_payment_id_seq", 1).block()
            resetSequence("table_payment_order_id_seq", 1).block()
            // Reset employee sequence
            try {
                resetSequence("staff_staff_id_seq", 1).block()
            } catch (e: Exception) {
                logger.warn("Failed to reset staff_staff_id_seq (this is OK if table doesn't exist yet): {}", e.message)
            }
            logger.info("Sequences reset successfully - all IDs will start from 1")
        } catch (e: Exception) {
            logger.warn("Failed to reset some sequences (this is OK if tables don't exist yet): {}", e.message)
        }
        
        logger.info("Data deleted successfully - starting with clean data (IDs starting from 1)")
        
        // Seed categories with hierarchy
        val categoryMap = seedCategories()
        
        // Seed allergen tags (EU 14 allergens) - must be done before products to get tag IDs
        val tagMap = seedAllergens()
        
        // Seed products with category assignments
        val productMap = seedProducts(categoryMap)
        
        // Associate allergens with products
        seedProductAllergens(productMap, tagMap)

        // Seed translations
        seedTranslations(categoryMap, productMap)

        // Seed tables (1..10) for each restaurant (create demo restaurants if none exist)
        seedTables()

        // Seed employees for restaurants
        seedEmployees()

        // Seed restaurant-product relationships with random availability
        seedRestaurantProducts(productMap)

        logger.info("Data seeding completed!")
    }

    private fun seedCategories(): Map<String, Long> {
        logger.info("Seeding categories with hierarchy...")
        val savedCategories = mutableMapOf<String, Long>()
        
        fun saveCategory(name: String, parentId: Long? = null, order: Int): Long {
            val category = categoryR2dbcRepository.save(CategoryEntity(name = name, parentId = parentId, order = order)).block()!!
            val id = category.id ?: throw IllegalStateException("Category id is null after save for $name")
            savedCategories[name] = id
            return id
        }
        
        // Root categories
        val foodId = saveCategory("Food", order = 1)
        val beveragesId = saveCategory("Beverages", order = 2)
        
        // Food subcategories
        val appetizersId = saveCategory("Appetizers", foodId, order = 1)
        val mainCoursesId = saveCategory("Main Courses", foodId, order = 2)
        val saladsId = saveCategory("Salads", foodId, order = 3)
        val dessertsId = saveCategory("Desserts", foodId, order = 4)
        
        // Appetizers sub-subcategories
        saveCategory("Bread & Starters", appetizersId, order = 1)
        saveCategory("Small Plates", appetizersId, order = 2)
        
        // Main Courses sub-subcategories
        saveCategory("Meat Dishes", mainCoursesId, order = 1)
        saveCategory("Seafood", mainCoursesId, order = 2)
        saveCategory("Pasta", mainCoursesId, order = 3)
        saveCategory("Vegetarian", mainCoursesId, order = 4)
        
        // Beverages subcategories
        saveCategory("Hot Drinks", beveragesId, order = 1)
        saveCategory("Cold Drinks", beveragesId, order = 2)
        saveCategory("Alcoholic", beveragesId, order = 3)

        // Desserts subcategories
        saveCategory("Cakes", dessertsId, order = 1)
        saveCategory("Ice Creams", dessertsId, order = 2)
        saveCategory("Pastries", dessertsId, order = 3)

        // Salads subcategories
        saveCategory("Green Salads", saladsId, order = 1)
        saveCategory("Fruit Salads", saladsId, order = 2)
        saveCategory("Protein Salads", saladsId, order = 3)
        
        logger.info("${savedCategories.size} categories seeded successfully")
        return savedCategories
    }

    private fun seedProducts(categoryMap: Map<String, Long>): Map<String, Int> {
        logger.info("Seeding products...")
        
        data class ProductData(
            val name: String,
            val price: String,
            val description: String,
            val imageUrl: String,
            val category: String
        )
        
        val productsData = listOf(
            ProductData("Garlic Bread", "4.50", "Fresh baked bread with garlic butter and herbs", 
                "https://images.unsplash.com/photo-1751199592465-f142293a8cc6?q=80&w=1168&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Bread & Starters"),
            ProductData("Bruschetta", "6.00", "Toasted bread topped with tomatoes, basil, and mozzarella",
                "https://images.unsplash.com/photo-1748718826530-06b08d46d078?q=80&w=724&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Bread & Starters"),
            ProductData("Chicken Wings", "8.50", "Crispy chicken wings with your choice of sauce",
                "https://images.unsplash.com/photo-1567620832903-9fc6debc209f?q=80&w=960&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Small Plates"),
            ProductData("Beef Steak", "24.00", "Premium ribeye steak cooked to your preference",
                "https://plus.unsplash.com/premium_photo-1723478557023-1f739ec06671?q=80&w=1672&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Meat Dishes"),
            ProductData("Chicken Parmesan", "16.50", "Breaded chicken breast with marinara sauce and mozzarella",
                "https://images.unsplash.com/photo-1632778149955-e80f8ceca2e8?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Meat Dishes"),
            ProductData("Grilled Salmon", "18.50", "Fresh Atlantic salmon grilled to perfection with lemon butter",
                "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Seafood"),
            ProductData("Shrimp Scampi", "17.00", "Tender shrimp in white wine and garlic sauce",
                "https://images.unsplash.com/photo-1598103442097-8b74394b95c6?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Seafood"),
            ProductData("Vegetarian Pasta", "14.00", "Penne pasta with seasonal vegetables in a light cream sauce",
                "https://media.istockphoto.com/id/1189709277/nl/foto/pasta-penne-met-geroosterde-tomaat-saus-mozzarella-kaas-grijze-stenen-achtergrond-bovenaanzicht.jpg?s=1024x1024&w=is&k=20&c=xdBy9QAifujU1gYAI0HSMQWzxuLKj4xfU3bqUhNNR4k=", "Pasta"),
            ProductData("Spaghetti Bolognese", "13.00", "Classic spaghetti with traditional meat sauce",
                "https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Pasta"),
            ProductData("Veggie Burger", "12.50", "House-made veggie patty with fresh toppings",
                "https://images.unsplash.com/photo-1520072959219-c595dc870360?q=80&w=1890&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Vegetarian"),
            ProductData("Caesar Salad", "9.50", "Fresh romaine lettuce with Caesar dressing and croutons",
                "https://images.unsplash.com/photo-1550304943-4f24f54ddde9?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Salads"),
            ProductData("Greek Salad", "10.50", "Mixed greens with feta cheese, olives, and Greek dressing",
                "https://plus.unsplash.com/premium_photo-1676047258557-de72954cf17c?q=80&w=878&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Salads"),
            ProductData("Caprese Salad", "11.00", "Fresh mozzarella, tomatoes, basil with balsamic glaze",
                "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Salads"),
            ProductData("Chocolate Cake", "6.50", "Rich chocolate cake with chocolate ganache",
                "https://images.unsplash.com/photo-1597083722160-c31d67d4af44?q=80&w=1816&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Desserts"),
            ProductData("Tiramisu", "7.00", "Classic Italian dessert with coffee and mascarpone",
                "https://plus.unsplash.com/premium_photo-1695028378225-97fbe39df62a?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Desserts"),
            ProductData("Ice Cream Sundae", "5.50", "Vanilla ice cream with chocolate sauce and whipped cream",
                "https://images.unsplash.com/photo-1657225953401-5f95007fc8e0?q=80&w=1738&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Desserts"),
            ProductData("Coffee", "2.50", "Freshly brewed espresso-based coffee",
                "https://images.unsplash.com/photo-1495774856032-8b90bbb32b32?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Hot Drinks"),
            ProductData("Cappuccino", "3.50", "Espresso with steamed milk and foam",
                "https://plus.unsplash.com/premium_photo-1669374537636-518629de3b85?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Hot Drinks"),
            ProductData("Hot Chocolate", "3.00", "Creamy hot chocolate with whipped cream",
                "https://images.unsplash.com/photo-1578985545062-69928b1d9587?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Hot Drinks"),
            ProductData("Fresh Orange Juice", "3.50", "Freshly squeezed orange juice",
                "https://images.unsplash.com/photo-1607690506833-498e04ab3ffa?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Cold Drinks"),
            ProductData("Sparkling Water", "2.00", "Refreshing sparkling water",
                "https://images.unsplash.com/photo-1619622683368-8a66b4b5c420?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Cold Drinks"),
            ProductData("Iced Tea", "2.75", "Chilled iced tea with fresh lemon",
                "https://images.unsplash.com/photo-1658397029207-029feea1ef25?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OXx8aWNldGVhfGVufDB8fDB8fHww", "Cold Drinks"),
            ProductData("House Red Wine", "5.50", "Selection of premium red wine by the glass",
                "https://images.unsplash.com/photo-1553361371-9b22f78e8b1d?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Alcoholic"),
            ProductData("House White Wine", "5.50", "Selection of premium white wine by the glass",
                "https://images.unsplash.com/photo-1681312913296-b656fa5ca865?w=800&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTV8fHdpdHRlJTIwd2lqbnxlbnwwfHwwfHx8MA%3D%3D", "Alcoholic"),
            ProductData("Craft Beer", "4.50", "Selection of local craft beers",
                "https://images.unsplash.com/photo-1594487984147-3389bcee5078?q=80&w=1160&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Alcoholic")
        )
        
        val products = productsData.map { data ->
            ProductEntity(
                name = data.name,
                price = BigDecimal(data.price),
                description = data.description,
                imageUrl = data.imageUrl,
                isAvailable = true,
                categoryId = categoryMap[data.category]?.toInt()
            )
        }
        
        val savedProducts = productR2dbcRepository.saveAll(products).collectList().block()!!
        logger.info("${savedProducts.size} products seeded successfully")
        return savedProducts.associate { it.name to (it.id ?: 0) }
    }

    private fun seedTranslations(categoryMap: Map<String, Long>, productMap: Map<String, Int>) {
        logger.info("Seeding translations...")
        
        // Category translations
        val categoryTranslations = mapOf(
            "Food" to "Eten",
            "Beverages" to "Dranken",
            "Appetizers" to "Voorgerechten",
            "Main Courses" to "Hoofdgerechten",
            "Salads" to "Salades",
            "Desserts" to "Nagerechten",
            "Bread & Starters" to "Brood & Voorgerechten",
            "Small Plates" to "Kleine Gerechten",
            "Meat Dishes" to "Vleesgerechten",
            "Seafood" to "Zeevruchten",
            "Pasta" to "Pasta",
            "Vegetarian" to "Vegetarisch",
            "Hot Drinks" to "Warme Dranken",
            "Cold Drinks" to "Koude Dranken",
            "Alcoholic" to "Alcoholisch",
            "Cakes" to "Taarten",
            "Ice Creams" to "IJsjes",
            "Pastries" to "Gebak",
            "Green Salads" to "Groene Salades",
            "Fruit Salads" to "Fruitsalades",
            "Protein Salads" to "Proteïne Salades"
        )
        
        // Product name + description translations
        data class ProductTranslation(
            val englishName: String,
            val dutchName: String,
            val englishDescription: String,
            val dutchDescription: String
        )
        
        val productTranslations = listOf(
            // Bread & Starters
            ProductTranslation(
                "Garlic Bread", 
                "Knoflookbrood",
                "Fresh baked bread with garlic butter and herbs",
                "Vers gebakken brood met knoflookboter en kruiden"
            ),
            ProductTranslation(
                "Bruschetta",
                "Bruschetta", 
                "Toasted bread topped with tomatoes, basil, and mozzarella",
                "Geroosterd brood met tomaten, basilicum en mozzarella"
            ),
            
            // Small Plates
            ProductTranslation(
                "Chicken Wings",
                "Kippenvleugels",
                "Crispy chicken wings with your choice of sauce",
                "Krokante kippenvleugels met saus naar keuze"
            ),
            
            // Meat Dishes
            ProductTranslation(
                "Beef Steak",
                "Biefstuk",
                "Premium ribeye steak cooked to your preference",
                "Premium ribeye biefstuk bereid naar wens"
            ),
            ProductTranslation(
                "Chicken Parmesan",
                "Kip Parmezaan",
                "Breaded chicken breast with marinara sauce and mozzarella",
                "Gepaneerde kipfilet met tomatensaus en mozzarella"
            ),
            
            // Seafood
            ProductTranslation(
                "Grilled Salmon",
                "Gegrilde Zalm",
                "Fresh Atlantic salmon grilled to perfection with lemon butter",
                "Verse Atlantische zalm gegrild met citroenboter"
            ),
            ProductTranslation(
                "Shrimp Scampi",
                "Scampi",
                "Tender shrimp in white wine and garlic sauce",
                "Malse garnalen in witte wijn en knoflooksaus"
            ),
            
            // Pasta
            ProductTranslation(
                "Vegetarian Pasta",
                "Vegetarische Pasta",
                "Penne pasta with seasonal vegetables in a light cream sauce",
                "Penne pasta met seizoensgroenten in lichte roomsaus"
            ),
            ProductTranslation(
                "Spaghetti Bolognese",
                "Spaghetti Bolognese",
                "Classic spaghetti with traditional meat sauce",
                "Klassieke spaghetti met traditionele vleessaus"
            ),
            
            // Vegetarian
            ProductTranslation(
                "Veggie Burger",
                "Veggie Burger",
                "House-made veggie patty with fresh toppings",
                "Huisgemaakte veggie burger met verse toppings"
            ),
            
            // Salads
            ProductTranslation(
                "Caesar Salad",
                "Caesar Salade",
                "Fresh romaine lettuce with Caesar dressing and croutons",
                "Verse romaine sla met Caesar dressing en croutons"
            ),
            ProductTranslation(
                "Greek Salad",
                "Griekse Salade",
                "Mixed greens with feta cheese, olives, and Greek dressing",
                "Gemengde sla met fetakaas, olijven en Griekse dressing"
            ),
            ProductTranslation(
                "Caprese Salad",
                "Caprese Salade",
                "Fresh mozzarella, tomatoes, basil with balsamic glaze",
                "Verse mozzarella, tomaten, basilicum met balsamico glazuur"
            ),
            
            // Desserts
            ProductTranslation(
                "Chocolate Cake",
                "Chocoladetaart",
                "Rich chocolate cake with chocolate ganache",
                "Rijke chocoladetaart met chocolade ganache"
            ),
            ProductTranslation(
                "Tiramisu",
                "Tiramisu",
                "Classic Italian dessert with coffee and mascarpone",
                "Klassiek Italiaans dessert met koffie en mascarpone"
            ),
            ProductTranslation(
                "Ice Cream Sundae",
                "IJscoupe",
                "Vanilla ice cream with chocolate sauce and whipped cream",
                "Vanille-ijs met chocoladesaus en slagroom"
            ),
            
            // Hot Drinks
            ProductTranslation(
                "Coffee",
                "Koffie",
                "Freshly brewed espresso-based coffee",
                "Vers gezette espresso-gebaseerde koffie"
            ),
            ProductTranslation(
                "Cappuccino",
                "Cappuccino",
                "Espresso with steamed milk and foam",
                "Espresso met gestoomde melk en melkschuim"
            ),
            ProductTranslation(
                "Hot Chocolate",
                "Warme Chocolademelk",
                "Creamy hot chocolate with whipped cream",
                "Romige warme chocolademelk met slagroom"
            ),
            
            // Cold Drinks
            ProductTranslation(
                "Fresh Orange Juice",
                "Vers Sinaasappelsap",
                "Freshly squeezed orange juice",
                "Vers geperst sinaasappelsap"
            ),
            ProductTranslation(
                "Sparkling Water",
                "Bruisend Water",
                "Refreshing sparkling water",
                "Verfrissend bruisend water"
            ),
            ProductTranslation(
                "Iced Tea",
                "IJsthee",
                "Chilled iced tea with fresh lemon",
                "Gekoelde ijsthee met verse citroen"
            ),
            
            // Alcoholic
            ProductTranslation(
                "House Red Wine",
                "Huiswijn Rood",
                "Selection of premium red wine by the glass",
                "Selectie van premium rode wijn per glas"
            ),
            ProductTranslation(
                "House White Wine",
                "Huiswijn Wit",
                "Selection of premium white wine by the glass",
                "Selectie van premium witte wijn per glas"
            ),
            ProductTranslation(
                "Craft Beer",
                "Speciaal Bier",
                "Selection of local craft beers",
                "Selectie van lokale speciaalbieren"
            )
        )
        // Tag (allergen) translations
        val tagTranslations = mapOf(
            "Celery" to "Selderij",
            "Mustard" to "Mosterd",
            "Sesame seeds" to "Sesamzaad",
            "Sulphur dioxide and sulphites" to "Zwaveldioxide en sulfieten",
            "Lupin" to "Lupine",
            "Molluscs" to "Weekdieren",
            "Cereals containing gluten" to "Gluten bevattende granen",
            "Crustaceans" to "Schaaldieren",
            "Eggs" to "Eieren",
            "Fish" to "Vis",
            "Peanuts" to "Pinda's",
            "Soybeans" to "Soja",
            "Milk" to "Melk",
            "Nuts" to "Noten"
    )
        
        val translations = mutableListOf<TranslationEntity>()
        
        // Category translations
        categoryTranslations.forEach { (englishName, dutchName) ->
            val categoryId = categoryMap[englishName]
            if (categoryId != null) {
                translations.add(TranslationEntity(
                    entityType = "category",
                    entityId = categoryId,
                    language = "en",
                    translation = englishName
                ))
                translations.add(TranslationEntity(
                    entityType = "category",
                    entityId = categoryId,
                    language = "nl",
                    translation = dutchName
                ))
            }
        }

        // Tag translations
        tagTranslations.forEach { (englishName, dutchName) ->
            val tag = tagR2dbcRepository.findAll()
                .filter { it.name == englishName }
                .blockFirst()
            
            if (tag != null && tag.id != null) {
                translations.add(TranslationEntity(
                    entityType = "tag",
                    entityId = tag.id!!.toLong(),
                    language = "en",
                    translation = englishName
                ))
                translations.add(TranslationEntity(
                    entityType = "tag",
                    entityId = tag.id!!.toLong(),
                    language = "nl",
                    translation = dutchName
                ))
        }
    }
        
        // Product name + description translations
        productTranslations.forEach { translation ->
            val productId = productMap[translation.englishName]
            if (productId != null && productId > 0) {
                // Name translations
                translations.add(TranslationEntity(
                    entityType = "product",
                    entityId = productId.toLong(),
                    language = "en",
                    translation = translation.englishName
                ))
                translations.add(TranslationEntity(
                    entityType = "product",
                    entityId = productId.toLong(),
                    language = "nl",
                    translation = translation.dutchName
                ))
                
                // Description translations
                translations.add(TranslationEntity(
                    entityType = "product_description",
                    entityId = productId.toLong(),
                    language = "en",
                    translation = translation.englishDescription
                ))
                translations.add(TranslationEntity(
                    entityType = "product_description",
                    entityId = productId.toLong(),
                    language = "nl",
                    translation = translation.dutchDescription
                ))
            }
        }
        
        translationR2dbcRepository.saveAll(translations).collectList().block()
        logger.info("${translations.size} translations seeded successfully")
    }

    private fun seedAllergens(): Map<String, Int> {
        logger.info("Seeding EU 14 allergen tags...")
        
        val allergenData = mapOf(
            "Cereals containing gluten" to AllergenSvgIcons.GLUTEN,
            "Crustaceans" to AllergenSvgIcons.CRUSTACEANS,
            "Eggs" to AllergenSvgIcons.EGGS,
            "Fish" to AllergenSvgIcons.FISH,
            "Peanuts" to AllergenSvgIcons.PEANUTS,
            "Soybeans" to AllergenSvgIcons.SOYBEANS,
            "Milk" to AllergenSvgIcons.MILK,
            "Nuts" to AllergenSvgIcons.NUTS,
            "Celery" to AllergenSvgIcons.CELERY,
            "Mustard" to AllergenSvgIcons.MUSTARD,
            "Sesame seeds" to AllergenSvgIcons.SESAME,
            "Sulphur dioxide and sulphites" to AllergenSvgIcons.SULPHITES,
            "Lupin" to AllergenSvgIcons.LUPIN,
            "Molluscs" to AllergenSvgIcons.MOLLUSCS
        )
        
        val allergens = allergenData.map { (name, icon) -> TagEntity(name = name, svgIcon = icon) }
        val savedTags = tagR2dbcRepository.saveAll(allergens).collectList().block()!!
        logger.info("${savedTags.size} allergen tags seeded successfully")
        return savedTags.associate { it.name to (it.id ?: 0) }
    }

    private fun seedProductAllergens(productMap: Map<String, Int>, tagMap: Map<String, Int>) {
        logger.info("Associating allergens with products...")
        
        // Define allergen associations based on product ingredients
        val productAllergenMap = mapOf(
            // Bread & Starters
            "Garlic Bread" to listOf("Cereals containing gluten", "Milk"),
            "Bruschetta" to listOf("Cereals containing gluten", "Milk"),
            
            // Small Plates
            "Chicken Wings" to listOf(), // Typically no common allergens, but sauces may vary
            
            // Meat Dishes
            "Beef Steak" to listOf(), // Typically no common allergens
            "Chicken Parmesan" to listOf("Cereals containing gluten", "Eggs", "Milk"),
            
            // Seafood
            "Grilled Salmon" to listOf("Fish"),
            "Shrimp Scampi" to listOf("Crustaceans", "Milk", "Sulphur dioxide and sulphites"),
            
            // Pasta
            "Vegetarian Pasta" to listOf("Cereals containing gluten", "Milk"),
            "Spaghetti Bolognese" to listOf("Cereals containing gluten", "Milk"),
            
            // Vegetarian
            "Veggie Burger" to listOf("Soybeans", "Cereals containing gluten", "Eggs"), // Common veggie burger ingredients
            
            // Salads
            "Caesar Salad" to listOf("Eggs", "Fish", "Milk", "Cereals containing gluten"), // Caesar dressing contains anchovies, eggs, cheese; croutons contain gluten
            "Greek Salad" to listOf("Milk"), // Feta cheese
            "Caprese Salad" to listOf("Milk"), // Mozzarella
            
            // Desserts
            "Chocolate Cake" to listOf("Cereals containing gluten", "Eggs", "Milk"),
            "Tiramisu" to listOf("Eggs", "Milk", "Cereals containing gluten"),
            "Ice Cream Sundae" to listOf("Milk"),
            
            // Hot Drinks
            "Coffee" to listOf(), // No common allergens
            "Cappuccino" to listOf("Milk"),
            "Hot Chocolate" to listOf("Milk"),
            
            // Cold Drinks
            "Fresh Orange Juice" to listOf(), // No common allergens
            "Sparkling Water" to listOf(), // No common allergens
            "Iced Tea" to listOf(), // No common allergens
            
            // Alcoholic
            "House Red Wine" to listOf("Sulphur dioxide and sulphites"),
            "House White Wine" to listOf("Sulphur dioxide and sulphites"),
            "Craft Beer" to listOf("Cereals containing gluten")
        )
        
        var associationsCount = 0
        productAllergenMap.forEach { (productName, allergenNames) ->
            val productId = productMap[productName]
            if (productId != null && productId > 0) {
                allergenNames.forEach { allergenName ->
                    val tagId = tagMap[allergenName]
                    if (tagId != null && tagId > 0) {
                        try {
                            productTagR2dbcRepository.insert(productId, tagId).block()
                            associationsCount++
                        } catch (e: Exception) {
                            logger.warn("Failed to associate allergen '$allergenName' with product '$productName': ${e.message}")
                        }
                    }
                }
            } else {
                logger.warn("Product '$productName' not found in product map")
            }
        }
        
        logger.info("$associationsCount allergen-product associations created successfully")
    }

    private fun seedTables() {
        logger.info("Seeding tables for each restaurant with different configurations...")

        val restaurants = restaurantR2dbcRepository.findAll().collectList().block() ?: emptyList()
        var restaurantList = restaurants

        if (restaurantList.isEmpty()) {
            // Create 2 demo restaurants for demonstration
            val demo1 = RestaurantEntity(
                name = "Demo Restaurant Centrum", 
                address = "Hoofdstraat 123, Amsterdam", 
                phone = "020-1234567", 
                email = "centrum@appetit.nl", 
                isActive = true
            )
            val demo2 = RestaurantEntity(
                name = "Demo Restaurant Zuid", 
                address = "Zuidplein 456, Amsterdam", 
                phone = "020-7654321", 
                email = "zuid@appetit.nl", 
                isActive = true
            )
            val savedDemo1 = restaurantR2dbcRepository.save(demo1).block()!!
            val savedDemo2 = restaurantR2dbcRepository.save(demo2).block()!!
            restaurantList = listOf(savedDemo1, savedDemo2)
            logger.info("No restaurants found. Created 2 demo restaurants: '${savedDemo1.name}' (id=${savedDemo1.id}) and '${savedDemo2.name}' (id=${savedDemo2.id})")
        }

        val tables = mutableListOf<TableEntity>()
        restaurantList.forEachIndexed { index, rest ->
            val rid = rest.id ?: return@forEachIndexed
            
            // Different configurations per restaurant for demonstration
            when (index) {
                0 -> {
                    // Restaurant 1 (Centrum): 10 tafels met capacities 2, 4, 6, 8
                    val capacities = listOf(2, 4, 6, 8)
                    for (i in 1..10) {
                        val capacity = capacities[(i - 1) % capacities.size]
                        tables.add(TableEntity(restaurantId = rid, tableNumber = i, capacity = capacity))
                    }
                }
                1 -> {
                    // Restaurant 2 (Zuid): 8 tafels met capacities 4, 6, 8 (geen 2-persoons tafels)
                    val capacities = listOf(4, 6, 8)
                    for (i in 1..8) {
                        val capacity = capacities[(i - 1) % capacities.size]
                        tables.add(TableEntity(restaurantId = rid, tableNumber = i, capacity = capacity))
                    }
                }
                else -> {
                    // Default: 10 tafels met capacity 4
                    for (i in 1..10) {
                        tables.add(TableEntity(restaurantId = rid, tableNumber = i, capacity = 4))
                    }
                }
            }
        }

        val savedTables = tableR2dbcRepository.saveAll(tables).collectList().block()!!
        
        // Log details per restaurant
        restaurantList.forEachIndexed { index, rest ->
            val rid = rest.id ?: return@forEachIndexed
            val restaurantTables = savedTables.filter { it.restaurantId == rid }
            val tableCount = restaurantTables.size
            val capacities = restaurantTables.mapNotNull { it.capacity }.distinct().sorted()
            logger.info("Restaurant '${rest.name}' (id=$rid): $tableCount tafels met capacities: ${capacities.joinToString(", ")}")
        }
        
        logger.info("${savedTables.size} tables seeded successfully for ${restaurantList.size} restaurant(s)")
    }

    private fun seedEmployees() {
        try {
            logger.info("Seeding employees for restaurants...")

            val restaurants = restaurantR2dbcRepository.findAll().collectList().block() ?: emptyList()
            val restaurantId = restaurants.firstOrNull()?.id

            if (restaurantId == null) {
                logger.warn("No restaurants found. Cannot seed employees without a restaurant.")
                return
            }

            // Check if employees already exist (for idempotency, but normally they should be deleted by cleanup)
            val existingEmployees = employeeR2dbcRepository.findAll().collectList().block() ?: emptyList()
            if (existingEmployees.isNotEmpty()) {
                logger.info("Employees already exist (${existingEmployees.size}). Skipping employee seeding.")
                logger.info("Note: If you want to reseed employees, delete them first or truncate the staff table.")
                return
            }

            val employees = mutableListOf<EmployeeEntity>()

            // Nederlandse namen met verschillende rollen voor AppetIT
            // Manager (alleen 1)
            employees.add(EmployeeEntity(
                firstName = "Jan",
                lastName = "van der Berg",
                personnelNumber = 1001,
                restaurantId = restaurantId,
                role = "MANAGER",
                active = true
            ))

            // Kitchen Chefs
            employees.add(EmployeeEntity(
                firstName = "Marieke",
                lastName = "de Vries",
                personnelNumber = 2001,
                restaurantId = restaurantId,
                role = "KITCHEN_CHEF",
                active = true
            ))
            employees.add(EmployeeEntity(
                firstName = "Daan",
                lastName = "Jansen",
                personnelNumber = 2002,
                restaurantId = restaurantId,
                role = "KITCHEN_CHEF",
                active = true
            ))

            // Bartenders
            employees.add(EmployeeEntity(
                firstName = "Sophie",
                lastName = "Bakker",
                personnelNumber = 3001,
                restaurantId = restaurantId,
                role = "BARTENDER",
                active = true
            ))
            employees.add(EmployeeEntity(
                firstName = "Luca",
                lastName = "Meijer",
                personnelNumber = 3002,
                restaurantId = restaurantId,
                role = "BARTENDER",
                active = true
            ))
            employees.add(EmployeeEntity(
                firstName = "Emma",
                lastName = "Visser",
                personnelNumber = 3003,
                restaurantId = restaurantId,
                role = "BARTENDER",
                active = true
            ))

            // Waiters
            employees.add(EmployeeEntity(
                firstName = "Tom",
                lastName = "Mulder",
                personnelNumber = 4001,
                restaurantId = restaurantId,
                role = "WAITER",
                active = true
            ))
            employees.add(EmployeeEntity(
                firstName = "Lisa",
                lastName = "Smit",
                personnelNumber = 4002,
                restaurantId = restaurantId,
                role = "WAITER",
                active = true
            ))
            employees.add(EmployeeEntity(
                firstName = "Noah",
                lastName = "de Boer",
                personnelNumber = 4003,
                restaurantId = restaurantId,
                role = "WAITER",
                active = true
            ))
            employees.add(EmployeeEntity(
                firstName = "Eva",
                lastName = "de Wit",
                personnelNumber = 4004,
                restaurantId = restaurantId,
                role = "WAITER",
                active = true
            ))

            // Cashiers
            employees.add(EmployeeEntity(
                firstName = "Lars",
                lastName = "de Jong",
                personnelNumber = 5001,
                restaurantId = restaurantId,
                role = "CASHIER",
                active = true
            ))
            employees.add(EmployeeEntity(
                firstName = "Anna",
                lastName = "Koning",
                personnelNumber = 5002,
                restaurantId = restaurantId,
                role = "CASHIER",
                active = true
            ))

            // Save all employees
            val savedEmployees = employeeR2dbcRepository.saveAll(employees).collectList().block() ?: emptyList()
            logger.info("Successfully seeded ${savedEmployees.size} employees:")
            
            savedEmployees.forEach { employee ->
                logger.info("  - ${employee.firstName} ${employee.lastName} (${employee.personnelNumber}) - ${employee.role}")
            }
            
        } catch (e: Exception) {
            logger.error("Error seeding employees: ${e.message}", e)
            // Don't throw - continue even if employee seeding fails
        }
    }

    private fun seedRestaurantProducts(productMap: Map<String, Int>) {
        logger.info("Seeding restaurant-product relationships with random availability...")

        val restaurants = restaurantR2dbcRepository.findAll().collectList().block() ?: emptyList()
        val products = productR2dbcRepository.findAll().collectList().block() ?: emptyList()

        if (restaurants.isEmpty()) {
            logger.warn("No restaurants found. Cannot seed restaurant-product relationships.")
            return
        }

        if (products.isEmpty()) {
            logger.warn("No products found. Cannot seed restaurant-product relationships.")
            return
        }

        val restaurantProducts = mutableListOf<RestaurantProductEntity>()
        
        // Use a seed for reproducibility (but still random per restaurant)
        val random = kotlin.random.Random(System.currentTimeMillis())

        restaurants.forEach { restaurant ->
            val restaurantId = restaurant.id ?: return@forEach
            
            products.forEach { product ->
                val productId = product.id ?: return@forEach
                
                // Generate random availability (70% chance of being available for demo purposes)
                // This creates variation between restaurants
                val isAvailable = random.nextDouble() < 0.7
                
                restaurantProducts.add(
                    RestaurantProductEntity(
                        restaurantId = restaurantId,
                        productId = productId,
                        customPrice = null, // No custom prices by default
                        isAvailable = isAvailable
                    )
                )
            }
        }

        // Delete existing restaurant-product relationships first
        try {
            restaurantProductR2dbcRepository.deleteAll().block()
        } catch (e: Exception) {
            logger.warn("Failed to delete existing restaurant-product relationships (this is OK on first run): {}", e.message)
        }

        val savedRestaurantProducts = restaurantProductR2dbcRepository.saveAll(restaurantProducts).collectList().block()!!
        
        // Count available vs unavailable per restaurant for logging
        restaurants.forEach { restaurant ->
            val restaurantId = restaurant.id ?: return@forEach
            val restaurantProductsForRestaurant = savedRestaurantProducts.filter { it.restaurantId == restaurantId }
            val availableCount = restaurantProductsForRestaurant.count { it.isAvailable }
            val unavailableCount = restaurantProductsForRestaurant.size - availableCount
            
            logger.info("Restaurant '${restaurant.name}' (id=$restaurantId): ${restaurantProductsForRestaurant.size} products linked (${availableCount} available, ${unavailableCount} unavailable)")
        }
        
        logger.info("${savedRestaurantProducts.size} restaurant-product relationships seeded successfully")
    }

    private fun resetSequence(sequenceName: String, startValue: Long): reactor.core.publisher.Mono<Long> {
        return databaseClient.sql("ALTER SEQUENCE $sequenceName RESTART WITH $startValue")
            .fetch()
            .rowsUpdated()
    }
}