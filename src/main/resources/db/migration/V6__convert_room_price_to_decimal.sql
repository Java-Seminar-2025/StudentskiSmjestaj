ALTER TABLE listing_rooms
ALTER COLUMN room_price TYPE DECIMAL(10,2)
USING room_price::DECIMAL(10,2);
