
-- Description: Consolidated insert scripts for CMS HR default data.

SYSTEM echo "INSERT HR OBJECT TYPES"
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_hr_object_types.sql
SYSTEM echo "INSERT HR OR TYPES"
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_hr_or_types.sql
SYSTEM echo "insert_civil_status";
source ~/CMS/src/eulap/eb/conf/db/data/insert_civil_status.sql
SYSTEM echo "insert_gender";
source ~/CMS/src/eulap/eb/conf/db/data/insert_gender.sql
SYSTEM echo "insert_default_employee_type"
source ~/CMS/src/eulap/eb/conf/db/data/insert_employee_type_default_type_18_00_04_26_2016.sql
SYSTEM echo "insert_default_holiday_type"
source ~/CMS/src/eulap/eb/conf/db/data/insert_holiday_types.sql
SYSTEM echo "Form Deduction Type Insert"
source ~/CMS/src/eulap/eb/conf/db/payroll/consolidated_insert_form_deduction_type_03_23_17_09_55_15.sql
SYSTEM echo "Inserting requeset types"
source ~/CMS/src/eulap/eb/conf/db/payroll/insert_request_type_03_23_2017_11_52.sql
SYSTEM echo "insert_employee_status"
source ~/CMS/src/eulap/eb/conf/db/data/insert_employee_status_04_27_2016_09_48.sql
SYSTEM echo "insert_biometric_model"
source ~/CMS/src/eulap/eb/conf/db/data/insert_biometric_models.sql
SYSTEM echo "INSERTING SALARY TYPES"
source ~/CMS/src/eulap/eb/conf/db/data/insert_salary_types.sql
SYSTEM echo "insert_educational_attainment_type"
source ~/CMS/src/eulap/eb/conf/db/data/insert_educational_attainment_type_10_31_2017_11_47_11.sql
SYSTEM echo "insert_employee_profile_last_form_object_or_types"
source ~/CMS/src/eulap/eb/conf/db/data/insert_employee_profile_last_form_object_or_types_01_25_2019_13_58.sql