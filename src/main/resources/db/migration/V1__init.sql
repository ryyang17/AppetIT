-- Restaurant table
CREATE TABLE restaurant (
    restaurant_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    phone VARCHAR(50),
    email VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Table table (restaurant tables)
CREATE TABLE "table" (
    table_id SERIAL PRIMARY KEY,
    restaurant_id INT NOT NULL REFERENCES restaurant(restaurant_id) ON DELETE CASCADE,
    table_number INT NOT NULL,
    capacity INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(restaurant_id, table_number)
);

-- Staff table
CREATE TABLE staff (
    staff_id SERIAL PRIMARY KEY,
    personnel_number VARCHAR(50) UNIQUE,
    restaurant_id INT REFERENCES restaurant(restaurant_id) ON DELETE SET NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Order table
CREATE TABLE "order" (
    order_id SERIAL PRIMARY KEY,
    table_id INT REFERENCES "table"(table_id) ON DELETE SET NULL,
    restaurant_id INT REFERENCES restaurant(restaurant_id) ON DELETE SET NULL,
    staff_id INT REFERENCES staff(staff_id) ON DELETE SET NULL,
    status VARCHAR(50),
    claimed_by_staff_id INT REFERENCES staff(staff_id) ON DELETE SET NULL,
    prepared_by_staff_id INT REFERENCES staff(staff_id) ON DELETE SET NULL,
    total_amount NUMERIC(10,2),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    completed_at TIMESTAMP
);

-- Category table
CREATE TABLE category (
    category_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Product table
CREATE TABLE product (
    product_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    description TEXT,
    is_available BOOLEAN DEFAULT TRUE,
    category_id INT REFERENCES category(category_id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- OrderItem table
CREATE TABLE order_item (
    order_item_id SERIAL PRIMARY KEY,
    order_id INT REFERENCES "order"(order_id) ON DELETE CASCADE,
    product_id INT REFERENCES product(product_id) ON DELETE SET NULL,
    staff_id INT REFERENCES staff(staff_id) ON DELETE SET NULL,
    quantity INT NOT NULL,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Payment table
CREATE TABLE payment (
    payment_id SERIAL PRIMARY KEY,
    order_id INT REFERENCES "order"(order_id) ON DELETE CASCADE,
    payment_method VARCHAR(50),
    amount NUMERIC(10,2),
    status VARCHAR(50),
    ideal_transaction_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    completed_at TIMESTAMP
);

-- Translations table
CREATE TABLE translations (
    translation_id SERIAL PRIMARY KEY,
    entity_type VARCHAR(50),
    entity_id INT,
    language VARCHAR(10),
    translation TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT unique_translation UNIQUE (entity_type, entity_id, language)
);

