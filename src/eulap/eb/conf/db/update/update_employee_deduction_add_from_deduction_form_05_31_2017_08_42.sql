
-- Description: Add FROM_DEDUCTION_FORM column.

ALTER TABLE EMPLOYEE_DEDUCTION ADD COLUMN FROM_DEDUCTION_FORM tinyint(1) DEFAULT NULL;