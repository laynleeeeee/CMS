
-- Description: Consolidated script to integrate Payroll

SYSTEM echo "======================================";
SYSTEM echo "INSERTING PAYROLL ADMIN MODULE SCRIPTS";
SYSTEM echo "======================================";
SYSTEM echo "Civil Status"
source ~/CMS/src/eulap/eb/conf/db/base/civil_status.sql
SYSTEM echo "Gender"
source ~/CMS/src/eulap/eb/conf/db/base/gender.sql
SYSTEM echo "biometric_model"
source ~/CMS/src/eulap/eb/conf/db/payroll/biometric_model.sql
SYSTEM echo "employee_shift"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_shift.sql
SYSTEM echo "employee_shift_additional_pay"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_shift_additional_pay.sql
SYSTEM echo "employee_shift_working_day"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_shift_working_day.sql
SYSTEM echo "employee_status"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_status.sql
SYSTEM echo "employee_type"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_type.sql
SYSTEM echo "employee"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee.sql
SYSTEM echo "salary_type"
source ~/CMS/src/eulap/eb/conf/db/payroll/salary_type.sql
SYSTEM echo "employee_salary_detail"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_salary_detail.sql
SYSTEM echo "payroll_time_period"
source ~/CMS/src/eulap/eb/conf/db/payroll/payroll_time_period.sql
SYSTEM echo "payroll_time_period_schedule"
source ~/CMS/src/eulap/eb/conf/db/payroll/payroll_time_period_schedule.sql
SYSTEM echo "holiday_type"
source ~/CMS/src/eulap/eb/conf/db/payroll/holiday_type.sql
SYSTEM echo "holiday_setting"
source ~/CMS/src/eulap/eb/conf/db/payroll/holiday_setting.sql
SYSTEM echo "daily_shift_schedule"
source ~/CMS/src/eulap/eb/conf/db/payroll/daily_shift_schedule.sql
SYSTEM echo "daily_shift_schedule_line"
source ~/CMS/src/eulap/eb/conf/db/payroll/daily_shift_schedule_line.sql

SYSTEM echo "======================================";
SYSTEM echo "INSERTING PAYROLL FORM MODULE SCRIPTS";
SYSTEM echo "======================================";
SYSTEM echo "Time sheet"
source ~/CMS/src/eulap/eb/conf/db/payroll/time_sheet.sql
SYSTEM echo "payroll"
source ~/CMS/src/eulap/eb/conf/db/payroll/payroll.sql
SYSTEM echo "payroll_employee_salary"
source ~/CMS/src/eulap/eb/conf/db/payroll/payroll_employee_salary.sql
SYSTEM echo "payroll_employee_time_sheet"
source ~/CMS/src/eulap/eb/conf/db/payroll/payroll_employee_time_sheet.sql
SYSTEM echo "Deduction Type"
source ~/CMS/src/eulap/eb/conf/db/payroll/deduction_type.sql
SYSTEM echo "Action Notice"
source ~/CMS/src/eulap/eb/conf/db/payroll/action_notice.sql
SYSTEM echo "Type of Leave"
source ~/CMS/src/eulap/eb/conf/db/payroll/type_of_leave.sql
SYSTEM echo "Document Type"
source ~/CMS/src/eulap/eb/conf/db/payroll/document_type.sql
SYSTEM echo "Employee Document"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_document.sql
SYSTEM echo "Employee Request"
source ~/CMS/src/eulap/eb/conf/db/payroll/consolidated_script_for_employee_request.sql
SYSTEM echo "Employee Leave Credit"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_leave_credit.sql
SYSTEM echo "Employee Leave Credit Line"
source ~/CMS/src/eulap/eb/conf/db/payroll/elc_line.sql
SYSTEM echo "Form Deduction Type"
source ~/CMS/src/eulap/eb/conf/db/payroll/form_deduction_type.sql
SYSTEM echo "Form Deduction"
source ~/CMS/src/eulap/eb/conf/db/payroll/form_deduction.sql
SYSTEM echo "Form Deduction Line"
source ~/CMS/src/eulap/eb/conf/db/payroll/form_deduction_line.sql
SYSTEM echo "Employee Profile"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_profile.sql
SYSTEM echo "Employee Family"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_family.sql
SYSTEM echo "Employee Sibling"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_sibling.sql
SYSTEM echo "Employee Children"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_children.sql
SYSTEM echo "Employee Employment"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_employment.sql
SYSTEM echo "Employee Educational Attainment Type"
source ~/CMS/src/eulap/eb/conf/db/payroll/educational_attainment_type.sql
SYSTEM echo "Employee Educational Attainment"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_educational_attainment.sql
SYSTEM echo "Employee Civil Query"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_civil_query.sql
SYSTEM echo "Employee Relative"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_relative.sql
SYSTEM echo "Employee Employement Query"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_employment_query.sql
SYSTEM echo "Personnel Action Notice"
source ~/CMS/src/eulap/eb/conf/db/payroll/personnel_action_notice.sql
SYSTEM echo "Location"
source ~/CMS/src/eulap/eb/conf/db/payroll/location.sql
SYSTEM echo "Employee DTR"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_dtr.sql
SYSTEM echo "Employee Deduction"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_deduction.sql
SYSTEM echo "Add Monthly Shift Schedule"
source ~/CMS/src/eulap/eb/conf/db/payroll/monthly_shift_schedule.sql
SYSTEM echo "Add Monthly Shift Schedule Line"
source ~/CMS/src/eulap/eb/conf/db/payroll/monthly_shift_schedule_line.sql
SYSTEM echo "employee_shift_day_off"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_shift_day_off.sql
SYSTEM echo "time_sheet_header"
source ~/CMS/src/eulap/eb/conf/db/payroll/time_sheet_header.sql
SYSTEM echo "employee_license_certificate"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_license_certificate.sql
SYSTEM echo "employee_emergency_contact"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_emergency_contact.sql
SYSTEM echo "employee_national_competency"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_national_competency.sql
SYSTEM echo "employee_seminar_attended"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_seminar_attended.sql

-- TODO: Fix if has error.
-- UPDATE EMPLOYEE_SALARY_DETAILS SET SALARY_TYPE_ID = 1 WHERE SALARY_TYPE_ID IS NULL;
