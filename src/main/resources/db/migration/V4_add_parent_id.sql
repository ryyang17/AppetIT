ALTER TABLE category
    ADD COLUMN parent_id BIGINT REFERENCES category(category_id) ON DELETE SET NULL;



