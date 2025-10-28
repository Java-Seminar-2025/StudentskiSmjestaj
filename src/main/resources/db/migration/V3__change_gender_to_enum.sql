ALTER TABLE student_details
DROP COLUMN gender;

ALTER TABLE student_details
ADD COLUMN gender gender NOT NULL;
