
-- Description	: Update script for October 29, 2021

-- Add accounts payable column to supplier advance payment account
source ~/CMS/src/eulap/eb/conf/db/nsb/data/update_supplier_adv_payment_account_add_ap_ac_id_10_28_2021_13_18.sql
-- Insert default accounts payable account combinations to supplier advance payment account
source ~/CMS/src/eulap/eb/conf/db/nsb/data/insert_default_ap_ac_id_for_sap_account_10_29_2021_14_02.sql