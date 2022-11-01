
-- Description: Modify CHECKBOOK_NUMBER to be able to save the large digits.

-- Changed datatype for Check Number
ALTER TABLE AP_PAYMENT MODIFY COLUMN CHECK_NUMBER DECIMAL(20,0);
