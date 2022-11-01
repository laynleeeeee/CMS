
-- Description	: Update script for September 27, 2021

-- Add currency rate to ap payment line.
source ~/CMS/src/eulap/eb/conf/db/nsb/data/update_ap_payment_line_add_currency_rate_value_09_21_2021_13_45.sql
-- Create ap payment account
source ~/CMS/src/eulap/eb/conf/db/disbursement/ap_payment_account.sql
-- Insert ap payment account data and add foreign exchange gain/loss accounts.
source ~/CMS/src/eulap/eb/conf/db/disbursement/data/insert_script_ap_payment_account_for_db_with_existing_data_09_27_2121_11_44.sql