-- Add comment and price columns to order_item table
ALTER TABLE order_item ADD COLUMN IF NOT EXISTS comment TEXT;
ALTER TABLE order_item ADD COLUMN IF NOT EXISTS price NUMERIC(10,2);

