
-- Description	: Update script for September 30, 2021

-- create project retention type
source ~/CMS/src/eulap/eb/conf/db/revenue/project_retention_type.sql
-- Insert default project retention types
source ~/CMS/src/eulap/eb/conf/db/nsb/data/insert_nsb_default_project_retention_type_09_27_2021_14_42.sql
-- Update project retention add retention type
source ~/CMS/src/eulap/eb/conf/db/nsb/data/update_project_retention_add_retention_type_09_27_14_53_2021.sql
-- Update ar invoice account add recoupment account combination
source ~/CMS/src/eulap/eb/conf/db/nsb/data/update_ar_invoice_account_add_recoupment_ac_id_09_30_2021_13_37.sql