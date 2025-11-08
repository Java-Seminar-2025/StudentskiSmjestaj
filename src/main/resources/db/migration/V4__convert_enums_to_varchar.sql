ALTER TABLE student_details
ALTER COLUMN gender TYPE VARCHAR(10)
USING gender::text;

ALTER TABLE listings
ALTER COLUMN status TYPE VARCHAR(20)
USING status::text;

ALTER TABLE reservations
ALTER COLUMN status TYPE VARCHAR(20)
USING status::text;
