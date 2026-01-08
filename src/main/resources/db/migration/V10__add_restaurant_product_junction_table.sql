-- Create restaurant_product junction table for many-to-many relationship
CREATE TABLE restaurant_product (
    id SERIAL PRIMARY KEY,
    restaurant_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    custom_price DECIMAL(10, 2), -- Optional restaurant-specific pricing
    is_available BOOLEAN NOT NULL DEFAULT true, -- Restaurant can disable specific products
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraints
    CONSTRAINT fk_restaurant_product_restaurant
        FOREIGN KEY (restaurant_id) REFERENCES restaurant(restaurant_id) ON DELETE CASCADE,
    CONSTRAINT fk_restaurant_product_product
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,

    -- Unique constraint to prevent duplicate restaurant-product combinations
    CONSTRAINT uk_restaurant_product UNIQUE (restaurant_id, product_id)
);

-- Index for performance
CREATE INDEX idx_restaurant_product_restaurant_id ON restaurant_product(restaurant_id);
CREATE INDEX idx_restaurant_product_product_id ON restaurant_product(product_id);
