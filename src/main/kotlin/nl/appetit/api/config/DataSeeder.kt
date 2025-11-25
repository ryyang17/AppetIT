package nl.appetit.api.config

import nl.appetit.api.config.AllergenSvgIcons
import nl.appetit.api.data.entity.CategoryEntity
import nl.appetit.api.data.entity.ProductEntity
import nl.appetit.api.data.entity.TagEntity
import nl.appetit.api.data.entity.TranslationEntity
import nl.appetit.api.data.repository.CategoryR2dbcRepository
import nl.appetit.api.data.repository.ProductR2dbcRepository
import nl.appetit.api.data.repository.TagR2dbcRepository
import nl.appetit.api.data.repository.ProductTagR2dbcRepository
import nl.appetit.api.data.repository.TranslationR2dbcRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.context.annotation.Profile
import java.math.BigDecimal

@Profile("!prod")
@Component
class DataSeeder(
    private val categoryR2dbcRepository: CategoryR2dbcRepository,
    private val productR2dbcRepository: ProductR2dbcRepository,
    private val tagR2dbcRepository: TagR2dbcRepository,
    private val productTagR2dbcRepository: ProductTagR2dbcRepository,
    private val translationR2dbcRepository: TranslationR2dbcRepository
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(DataSeeder::class.java)

    override fun run(vararg args: String) {
        logger.info("Starting data seeding...")
        
        // Delete all existing data
        logger.info("Deleting all existing data...")
        productR2dbcRepository.deleteAll().block()
        categoryR2dbcRepository.deleteAll().block()
        tagR2dbcRepository.deleteAll().block()
        translationR2dbcRepository.deleteAll().block()
        logger.info("Data deleted successfully")
        
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
        
        logger.info("Data seeding completed!")
    }

    private fun seedCategories(): Map<String, Long> {
        logger.info("Seeding categories with hierarchy...")
        val savedCategories = mutableMapOf<String, Long>()
        
        fun saveCategory(name: String, parentId: Long? = null, order: Int): Long {
            val category = categoryR2dbcRepository.save(CategoryEntity(name = name, parentId = parentId, order = order)).block()!!
            savedCategories[name] = category.id!!
            return category.id!!
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
                "https://images.unsplash.com/photo-1585238341710-4dd9e42e1e9a?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Vegetarian"),
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
                "https://images.unsplash.com/photo-1517668808822-9ebb02ae2a0e?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Hot Drinks"),
            ProductData("Hot Chocolate", "3.00", "Creamy hot chocolate with whipped cream",
                "https://images.unsplash.com/photo-1578985545062-69928b1d9587?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Hot Drinks"),
            ProductData("Fresh Orange Juice", "3.50", "Freshly squeezed orange juice",
                "https://images.unsplash.com/photo-1607690506833-498e04ab3ffa?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Cold Drinks"),
            ProductData("Sparkling Water", "2.00", "Refreshing sparkling water",
                "https://images.unsplash.com/photo-1619622683368-8a66b4b5c420?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Cold Drinks"),
            ProductData("Iced Tea", "2.75", "Chilled iced tea with fresh lemon",
                "https://images.unsplash.com/photo-1570020176750-e0dd52f6a83d?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Cold Drinks"),
            ProductData("House Red Wine", "5.50", "Selection of premium red wine by the glass",
                "https://images.unsplash.com/photo-1510812431401-41d2cab2707d?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Alcoholic"),
            ProductData("House White Wine", "5.50", "Selection of premium white wine by the glass",
                "https://images.unsplash.com/photo-1510812431401-41d2cab2707d?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Alcoholic"),
            ProductData("Craft Beer", "4.50", "Selection of local craft beers",
                "https://images.unsplash.com/photo-1608270861620-7c40f36e1b5d?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", "Alcoholic")
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
        val productTranslations = mapOf(
            "Garlic Bread" to "Knoflookbrood",
            "Bruschetta" to "Bruschetta",
            "Chicken Wings" to "Kippenvleugels",
            "Beef Steak" to "Biefstuk",
            "Chicken Parmesan" to "Kip Parmezaan",
            "Grilled Salmon" to "Gegrilde Zalm",
            "Shrimp Scampi" to "Scampi",
            "Vegetarian Pasta" to "Vegetarische Pasta",
            "Spaghetti Bolognese" to "Spaghetti Bolognese",
            "Veggie Burger" to "Veggie Burger",
            "Caesar Salad" to "Caesar Salade",
            "Greek Salad" to "Griekse Salade",
            "Caprese Salad" to "Caprese Salade",
            "Chocolate Cake" to "Chocoladetaart",
            "Tiramisu" to "Tiramisu",
            "Ice Cream Sundae" to "IJscoupe",
            "Coffee" to "Koffie",
            "Cappuccino" to "Cappuccino",
            "Hot Chocolate" to "Warme Chocolademelk",
            "Fresh Orange Juice" to "Vers Sinaasappelsap",
            "Sparkling Water" to "Bruisend Water",
            "Iced Tea" to "IJsthee",
            "House Red Wine" to "Huiswijn Rood",
            "House White Wine" to "Huiswijn Wit",
            "Craft Beer" to "Speciaal Bier"
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
        // Product translations
        productTranslations.forEach { (englishName, dutchName) ->
            val productId = productMap[englishName]
            if (productId != null && productId > 0) {
                translations.add(TranslationEntity(
                    entityType = "product",
                    entityId = productId.toLong(),
                    language = "en",
                    translation = englishName
                ))
                translations.add(TranslationEntity(
                    entityType = "product",
                    entityId = productId.toLong(),
                    language = "nl",
                    translation = dutchName
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
    
}