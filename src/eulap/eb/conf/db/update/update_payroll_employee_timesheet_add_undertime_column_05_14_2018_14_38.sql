
-- Description : Update script that will add UNDERTIME column in PAYROLL_EMPLOYEE_TIMESHEET Table.

ALTER TABLE PAYROLL_EMPLOYEE_TIMESHEET ADD COLUMN UNDERTIME DOUBLE(4,2) DEFAULT 0;