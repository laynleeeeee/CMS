
-- Description	: Update script for October 11, 2021

source ~/CMS/src/eulap/eb/conf/db/accounting/loan_proceeds.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/lp_line.sql
source ~/CMS/src/eulap/eb/conf/db/data/insert_laon_proceeds_object_types.sql

-- Add currency rate value column.
source ~/CMS/src/eulap/eb/conf/db/nsb/data/update_ar_receipt_line_add_currency_rate_value_10_07_2021_13_10.sql
source ~/CMS/src/eulap/eb/conf/db/nsb/data/update_project_retention_line_add_currency_rate_value_10_08_2021_08_39.sql
-- Create project retention line.
source ~/CMS/src/eulap/eb/conf/db/disbursement/project_retention_line.sql
-- Create ar receipt account.
source ~/CMS/src/eulap/eb/conf/db/accounting/ar_receipt_account.sql
-- Insert default accounts for ar receipt account and project retention account.
source ~/CMS/src/eulap/eb/conf/db/nsb/data/insert_default_ar_receipt_account_10_11_2021_14_07.sql
source ~/CMS/src/eulap/eb/conf/db/nsb/data/insert_default_project_retention_account_10_11_2021_14_8.sql
-- Insert project retention type to ar receipt line type table.
source ~/CMS/src/eulap/eb/conf/db/nsb/data/insert_ar_receipt_line_type_project_retention_10_12_2021_08_26.sql