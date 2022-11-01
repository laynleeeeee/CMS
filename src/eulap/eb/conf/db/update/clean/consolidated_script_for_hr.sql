
-- Description: Create script for HR modules.

source ~/CMS/src/eulap/eb/conf/db/payroll/consolidated_payroll_create_script.sql

SYSTEM echo "update_r_purchase_order_add_requested_by_column_04_30_2018_10_25.sql";
source ~/CMS/src/eulap/eb/conf/db/update/update_r_purchase_order_add_requested_by_column_04_30_2018_10_25.sql

SYSTEM echo "update_r_purchase_order_add_column_requester_name_05_31_2018_13_50.sql";
source ~/CMS/src/eulap/eb/conf/db/update/update_r_purchase_order_add_column_requester_name_05_31_2018_13_50.sql

SYSTEM echo "update_daily_shift_schedule_add_eb_object_id_12_16_2019.sql";
source ~/CMS/src/eulap/eb/conf/db/payroll/update_daily_shift_schedule_add_eb_object_id_12_16_2019.sql

SYSTEM echo "update_payroll_employee_salary_add_balances_03_11_2020_09_44.sql";
source ~/CMS/src/eulap/eb/conf/db/payroll/update_payroll_employee_salary_add_balances_03_11_2020_09_44.sql

SYSTEM echo "==================================="
SYSTEM echo "==========HR DEFAULT DATA=========="
SYSTEM echo "==================================="
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_default_hr_data.sql

-- enable eulap default parser only
UPDATE BIOMETRIC_MODEL SET ACTIVE=0 WHERE BIOMETRIC_MODEL_ID != 11;

ALTER TABLE PAYROLL MODIFY COLUMN EMPLOYEE_TYPE_ID INT (10) UNSIGNED DEFAULT NULL;

ALTER TABLE EMPLOYEE_DTR ADD COLUMN PAYROLL_EMPLOYEE_TIMESHEET_ID INT(10) UNSIGNED NULL AFTER LOCATION_ID,
ADD CONSTRAINT FK_EDRT_PAYROLL_EMPLOYEE_TIMESHEET_ID FOREIGN KEY (PAYROLL_EMPLOYEE_TIMESHEET_ID)
REFERENCES PAYROLL_EMPLOYEE_TIMESHEET (PAYROLL_EMPLOYEE_TIMESHEET_ID);

ALTER TABLE EMPLOYEE_LEAVE_CREDIT ADD DIVISION_ID int(10) unsigned NOT NULL AFTER COMPANY_ID,
ADD CONSTRAINT FK_ELC_DIVISION_ID FOREIGN KEY (DIVISION_ID) REFERENCES DIVISION (DIVISION_ID);

ALTER TABLE DAILY_SHIFT_SCHEDULE MODIFY DIVISION_ID INT(10) UNSIGNED DEFAULT NULL;

ALTER TABLE PAYROLL_EMPLOYEE_TIMESHEET ADD COLUMN DAYS_WORKED DOUBLE(4,2) NULL DEFAULT 0.00 AFTER STATUS_LABEL;

ALTER TABLE PAYROLL_EMPLOYEE_SALARY ADD COLUMN ADJUSTMENT DOUBLE(12,2) DEFAULT NULL;
ALTER TABLE PAYROLL_EMPLOYEE_SALARY ADD COLUMN DAYS DOUBLE(12,2) DEFAULT NULL;
ALTER TABLE PAYROLL_EMPLOYEE_SALARY ADD COLUMN DAILY_SALARY DOUBLE(12,2) DEFAULT NULL;

ALTER TABLE PAYROLL_EMPLOYEE_SALARY ADD COLUMN REGULAR_HOLIDAY_PAY DOUBLE(12,2) DEFAULT NULL;

INSERT INTO OBJECT_TYPE VALUES (7000, 'DAILY_SHIFT_SCHEDULE', 'DAILY_SHIFT_SCHEDULE', '', 1, 1, NOW(), 1, NOW());
