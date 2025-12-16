-- TablePayment: one payment per table, grouping multiple orders
CREATE TABLE table_payment (
    table_payment_id SERIAL PRIMARY KEY,
    table_id INT REFERENCES "table"(table_id) ON DELETE SET NULL,
    total_amount NUMERIC(10,2) NOT NULL,
    payment_method VARCHAR(50),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Link table between table_payment and orders
CREATE TABLE table_payment_order (
    id SERIAL PRIMARY KEY,
    table_payment_id INT REFERENCES table_payment(table_payment_id) ON DELETE CASCADE,
    order_id INT REFERENCES "order"(order_id) ON DELETE CASCADE
);

