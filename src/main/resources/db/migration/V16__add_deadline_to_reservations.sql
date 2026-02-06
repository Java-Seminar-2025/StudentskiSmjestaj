ALTER TABLE reservations
ADD COLUMN accepted_at TIMESTAMP;

ALTER TABLE reservations
ADD COLUMN cancellation_deadline TIMESTAMP;