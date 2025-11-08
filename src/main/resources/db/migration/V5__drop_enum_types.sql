ALTER TABLE listings
ALTER COLUMN preferred_gender TYPE VARCHAR(10)
USING preferred_gender::text;

DROP TYPE IF EXISTS gender;
DROP TYPE IF EXISTS listing_status;
DROP TYPE IF EXISTS reservation_status;