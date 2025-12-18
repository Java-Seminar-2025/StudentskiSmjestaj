ALTER TABLE student_details
DROP CONSTRAINT student_details_pkey;

ALTER TABLE student_details
ADD COLUMN id SERIAL PRIMARY KEY;
