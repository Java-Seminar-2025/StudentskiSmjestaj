ALTER TABLE reservations
ALTER COLUMN room_id DROP NOT NULL;

ALTER TABLE reservations
ADD COLUMN listing_id INT,
ADD COLUMN type VARCHAR(20);

UPDATE reservations r
SET listing_id = lr.listing_id
FROM listing_rooms lr
WHERE r.room_id = lr.room_id;

UPDATE reservations
SET type = 'ROOM'
WHERE room_id IS NOT NULL;

ALTER TABLE reservations
ALTER COLUMN listing_id SET NOT NULL,
ALTER COLUMN type SET NOT NULL;

ALTER TABLE reservations
ADD CONSTRAINT fk_reservation_listing FOREIGN KEY (listing_id) REFERENCES listings(listing_id);