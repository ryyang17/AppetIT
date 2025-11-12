-- Change personnel_number from VARCHAR to INT on staff table
ALTER TABLE staff
    ALTER COLUMN personnel_number TYPE INT USING personnel_number::INT;

-- Insert a default restaurant with id 1 for testing
INSERT INTO restaurant (restaurant_id, name, address, phone, email, is_active, created_at, updated_at)
VALUES (1, 'Default Restaurant', '123 Main Street', '+31612345678', 'info@restaurant.nl', TRUE, NOW(), NOW())
ON CONFLICT (restaurant_id) DO NOTHING;

-- Reset the sequence to continue from id 2 onwards
SELECT setval('restaurant_restaurant_id_seq', (SELECT MAX(restaurant_id) FROM restaurant));
