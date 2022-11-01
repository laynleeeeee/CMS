
-- Description: Modify CHECKBOOK_NO_FROM and CHECKBOOK_NO_TO to be able to save the large digits.
-- Limit was also expanded from 10 to 20 digits.

-- Changed datatype for Check number FROM
ALTER TABLE CHECKBOOK MODIFY COLUMN CHECKBOOK_NO_FROM DECIMAL(20,0);

-- Changed datatype for Check number TO
ALTER TABLE CHECKBOOK MODIFY COLUMN CHECKBOOK_NO_TO DECIMAL(20,0);