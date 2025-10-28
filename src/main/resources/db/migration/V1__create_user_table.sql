CREATE TYPE user_role AS ENUM('student','landlord');

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    surname VARCHAR(30) NOT NULL,
    email VARCHAR(30) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    phone_number INT
);