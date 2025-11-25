-- V8: Create triggers for automatic translations

-- Function to add translations for categories
CREATE OR REPLACE FUNCTION add_category_translations()
RETURNS TRIGGER AS $$
BEGIN
    -- Add English translation (use the name as-is)
    INSERT INTO translations (entity_type, entity_id, language, translation)
    VALUES ('category', NEW.category_id, 'en', NEW.name)
    ON CONFLICT (entity_type, entity_id, language) DO NOTHING;
    
    -- Add Dutch translation based on common mappings
    INSERT INTO translations (entity_type, entity_id, language, translation)
    VALUES ('category', NEW.category_id, 'nl', 
        CASE NEW.name
            WHEN 'Food' THEN 'Eten'
            WHEN 'Beverages' THEN 'Dranken'
            WHEN 'Appetizers' THEN 'Voorgerechten'
            WHEN 'Main Courses' THEN 'Hoofdgerechten'
            WHEN 'Salads' THEN 'Salades'
            WHEN 'Desserts' THEN 'Nagerechten'
            WHEN 'Bread & Starters' THEN 'Brood & Voorgerechten'
            WHEN 'Small Plates' THEN 'Kleine Gerechten'
            WHEN 'Meat Dishes' THEN 'Vleesgerechten'
            WHEN 'Seafood' THEN 'Zeevruchten'
            WHEN 'Pasta' THEN 'Pasta'
            WHEN 'Vegetarian' THEN 'Vegetarisch'
            WHEN 'Hot Drinks' THEN 'Warme Dranken'
            WHEN 'Cold Drinks' THEN 'Koude Dranken'
            WHEN 'Alcoholic' THEN 'Alcoholisch'
            WHEN 'Cakes' THEN 'Taarten'
            WHEN 'Ice Creams' THEN 'IJsjes'
            WHEN 'Pastries' THEN 'Gebak'
            WHEN 'Green Salads' THEN 'Groene Salades'
            WHEN 'Fruit Salads' THEN 'Fruitsalades'
            WHEN 'Protein Salads' THEN 'Proteïne Salades'
            ELSE NEW.name
        END
    )
    ON CONFLICT (entity_type, entity_id, language) DO NOTHING;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Function to add translations for products
CREATE OR REPLACE FUNCTION add_product_translations()
RETURNS TRIGGER AS $$
BEGIN
    -- Add English translation
    INSERT INTO translations (entity_type, entity_id, language, translation)
    VALUES ('product', NEW.product_id, 'en', NEW.name)
    ON CONFLICT (entity_type, entity_id, language) DO NOTHING;
    
    -- Add Dutch translation
    INSERT INTO translations (entity_type, entity_id, language, translation)
    VALUES ('product', NEW.product_id, 'nl',
        CASE NEW.name
            -- Bread & Starters
            WHEN 'Garlic Bread' THEN 'Knoflookbrood'
            WHEN 'Bruschetta' THEN 'Bruschetta'
            -- Small Plates
            WHEN 'Chicken Wings' THEN 'Kippenvleugels'
            -- Meat Dishes
            WHEN 'Beef Steak' THEN 'Biefstuk'
            WHEN 'Chicken Parmesan' THEN 'Kip Parmezaan'
            -- Seafood
            WHEN 'Grilled Salmon' THEN 'Gegrilde Zalm'
            WHEN 'Shrimp Scampi' THEN 'Scampi'
            -- Pasta
            WHEN 'Vegetarian Pasta' THEN 'Vegetarische Pasta'
            WHEN 'Spaghetti Bolognese' THEN 'Spaghetti Bolognese'
            -- Vegetarian
            WHEN 'Veggie Burger' THEN 'Veggie Burger'
            -- Salads
            WHEN 'Caesar Salad' THEN 'Caesar Salade'
            WHEN 'Greek Salad' THEN 'Griekse Salade'
            WHEN 'Caprese Salad' THEN 'Caprese Salade'
            -- Desserts/Cakes
            WHEN 'Chocolate Cake' THEN 'Chocoladetaart'
            WHEN 'Tiramisu' THEN 'Tiramisu'
            WHEN 'Ice Cream Sundae' THEN 'IJscoupe'
            -- Hot Drinks
            WHEN 'Coffee' THEN 'Koffie'
            WHEN 'Cappuccino' THEN 'Cappuccino'
            WHEN 'Hot Chocolate' THEN 'Warme Chocolademelk'
            -- Cold Drinks
            WHEN 'Fresh Orange Juice' THEN 'Vers Sinaasappelsap'
            WHEN 'Sparkling Water' THEN 'Bruisend Water'
            ELSE NEW.name
        END
    )
    ON CONFLICT (entity_type, entity_id, language) DO NOTHING;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers
DROP TRIGGER IF EXISTS category_translation_trigger ON category;
CREATE TRIGGER category_translation_trigger
    AFTER INSERT ON category
    FOR EACH ROW
    EXECUTE FUNCTION add_category_translations();

DROP TRIGGER IF EXISTS product_translation_trigger ON product;
CREATE TRIGGER product_translation_trigger
    AFTER INSERT ON product
    FOR EACH ROW
    EXECUTE FUNCTION add_product_translations();

