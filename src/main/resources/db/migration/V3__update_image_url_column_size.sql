-- Update image_url column size from VARCHAR(128) to VARCHAR(512)
ALTER TABLE product 
ALTER COLUMN image_url TYPE VARCHAR(512);
