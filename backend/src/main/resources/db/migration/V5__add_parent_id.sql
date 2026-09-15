-- db/migration/V5__add_parent_id.sql
ALTER TABLE category ADD COLUMN IF NOT EXISTS parent_id BIGINT;
