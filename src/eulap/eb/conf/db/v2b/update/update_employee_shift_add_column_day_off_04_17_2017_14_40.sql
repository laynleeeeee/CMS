
-- Description: Alter table EMPLOYEE_SHIFT add column day off.

ALTER TABLE EMPLOYEE_SHIFT ADD COLUMN DAY_OFF tinyint(1) DEFAULT 0 AFTER NIGHT_SHIFT