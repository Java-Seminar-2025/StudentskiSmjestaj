ALTER TABLE listings
ADD COLUMN days_to_cancel INT;

UPDATE listings
SET days_to_cancel = DATE_PART('day', cancellation_deadline - CURRENT_TIMESTAMP);

ALTER TABLE listings
DROP COLUMN cancellation_deadline;