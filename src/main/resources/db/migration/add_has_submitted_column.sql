-- Migration script to add has_submitted column to existing user_progress table
-- This is a safe migration that preserves existing data
-- For SQLite: This adds the column with default value FALSE for all existing records

-- Add the has_submitted column if it doesn't exist
-- Set default to FALSE for existing records
ALTER TABLE user_progress ADD COLUMN has_submitted BOOLEAN DEFAULT FALSE NOT NULL;
