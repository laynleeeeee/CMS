
-- Description	: Alter script that will add OTHER_DEDUCTION, CA_BALANCE, and OTHER_BALANCE

ALTER TABLE PAYROLL_EMPLOYEE_SALARY ADD COLUMN OTHER_DEDUCTION DOUBLE(12,2) DEFAULT '0';
ALTER TABLE PAYROLL_EMPLOYEE_SALARY ADD COLUMN CA_BALANCE DOUBLE(12,2) DEFAULT '0';
ALTER TABLE PAYROLL_EMPLOYEE_SALARY ADD COLUMN OTHER_BALANCE DOUBLE(12,2) DEFAULT '0';