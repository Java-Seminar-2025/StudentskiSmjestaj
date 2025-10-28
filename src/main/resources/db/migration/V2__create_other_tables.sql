CREATE TYPE gender AS ENUM('male','female');
CREATE TYPE listing_status AS ENUM('available','partially occupied','occupied');
CREATE TYPE reservation_status AS ENUM('pending confirmation','active','cancelled','completed');

CREATE TABLE listings (
    listing_id SERIAL PRIMARY KEY,
    landlord_id INT NOT NULL,
    title VARCHAR(30) NOT NULL,
    description TEXT NOT NULL,
    address VARCHAR(40) NOT NULL,
    city VARCHAR(30) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    number_of_students INT NOT NULL,
    number_of_rooms INT NOT NULL,
    preferred_gender gender,
    status listing_status NOT NULL,
    cancellation_deadline TIMESTAMP,
    CONSTRAINT fk_listing_landlord FOREIGN KEY (landlord_id) REFERENCES users(user_id)
);

CREATE TABLE favorites (
    favorite_listing_id SERIAL PRIMARY KEY,
    student_id INT NOT NULL,
    listing_id INT NOT NULL,
    CONSTRAINT fk_student_favorite FOREIGN KEY (student_id) REFERENCES users(user_id),
    CONSTRAINT fk_listing_favorite FOREIGN KEY (listing_id) REFERENCES listings(listing_id)
);

CREATE TABLE listing_rooms (
    room_id SERIAL PRIMARY KEY,
    listing_id INT NOT NULL,
    room_price INT NOT NULL,
    capacity INT NOT NULL,
    CONSTRAINT fk_room_listing FOREIGN KEY (listing_id) REFERENCES listings(listing_id)
);

CREATE TABLE reservations (
    reservation_id SERIAL PRIMARY KEY,
    room_id INT NOT NULL,
    student_id INT NOT NULL,
    status reservation_status NOT NULL,
    created_at TIMESTAMP NOT NULL,
    cancelled_at TIMESTAMP,
    CONSTRAINT fk_reservation_room FOREIGN KEY (room_id) REFERENCES listing_rooms(room_id),
    CONSTRAINT fk_reservation_student FOREIGN KEY (student_id) REFERENCES users(user_id)
);

CREATE TABLE faculties (
    faculty_id SERIAL PRIMARY KEY,
    name VARCHAR(40) UNIQUE NOT NULL,
    address VARCHAR(40) NOT NULL,
    city VARCHAR(30) NOT NULL
);

CREATE TABLE student_details (
    student_id INT PRIMARY KEY,
    faculty_id INT NOT NULL,
    year_of_study INT,
    gender INT NOT NULL,
    CONSTRAINT fk_student_faculty FOREIGN KEY (faculty_id) REFERENCES faculties(faculty_id),
    CONSTRAINT fk_student_user FOREIGN KEY (student_id) REFERENCES users(user_id)
);

CREATE TABLE room_occupants (
    id SERIAL PRIMARY KEY,
    room_id INT NOT NULL,
    student_id INT NOT NULL,
    CONSTRAINT fk_room_id FOREIGN KEY (room_id) REFERENCES listing_rooms(room_id),
    CONSTRAINT fk_room_student FOREIGN KEY (student_id) REFERENCES users(user_id)
);