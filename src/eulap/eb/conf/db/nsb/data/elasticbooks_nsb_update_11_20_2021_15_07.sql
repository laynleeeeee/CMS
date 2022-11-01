
-- Description	: Update script for November 20, 2021

-- Loan Proceeds
source ~/CMS/src/eulap/eb/conf/db/accounting/loan_proceeds.sql

source ~/CMS/src/eulap/eb/conf/db/accounting/lp_line.sql

source ~/CMS/src/eulap/eb/conf/db/data/insert_laon_proceeds_object_types.sql

source ~/CMS/src/eulap/eb/conf/db/revenue/loan_proceeds_type.sql

source ~/CMS/src/eulap/eb/conf/db/nsb/data/insert_nsb_default_loan_proceeds_type_10_13_2021_14_37.sql

source ~/CMS/src/eulap/eb/conf/db/nsb/data/update_loan_proceeds_add_type_id_10_13_2021_14_39.sql

source ~/CMS/src/eulap/eb/conf/db/nsb/data/update_loan_proceeds_add_loan_account_11_20_2-21_09_43.sql

-- AP Loan
source ~/CMS/src/eulap/eb/conf/db/nsb/data/insert_loan_proceeds_ap_loan_or_type_11_17_2021_08_38.sql

source ~/CMS/src/eulap/eb/conf/db/nsb/data/insert_ap_loan_invoice_types_11_24_2021_15_34.sql

source ~/CMS/src/eulap/eb/conf/db/nsb/data/update_ap_invoice_add_principal_amount_11_15_2021_15_15.sql

source ~/CMS/src/eulap/eb/conf/db/nsb/data/update_ap_invoice_add_loan_acct_id_11_20_2021_12_34.sql

-- Rename EWT WI/WC 160 account.
source ~/CMS/src/eulap/eb/conf/db/nsb/data/update_ewt_wi_wc_160_account_name_11_19_2021_14_47.sql