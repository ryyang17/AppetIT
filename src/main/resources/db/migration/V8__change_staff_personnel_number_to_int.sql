-- Change personnel_number from VARCHAR to INT on staff table
ALTER TABLE staff
    ALTER COLUMN personnel_number TYPE INT USING personnel_number::INT;

