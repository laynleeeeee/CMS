
-- Description: Create script for NSB clean DB.

SYSTEM echo "CMS_nsb_clean_08_11_2021_09_35.sql"
source ~/CMS/src/eulap/eb/conf/db/nsb/clean/CMS_nsb_clean_08_11_2021_09_35.sql

SYSTEM echo "Create AP Payment Line"
source ~/CMS/src/eulap/eb/conf/db/accounting/ap_payment_line.sql

SYSTEM echo "Create Supplier Advance Payment Line"
source ~/CMS/src/eulap/eb/conf/db/disbursement/supplier_advance_payment_line.sql

SYSTEM echo "Create SO Type"
source ~/CMS/src/eulap/eb/conf/db/revenue/so_type.sql
source ~/CMS/src/eulap/eb/conf/db/data/insert_so_type_06_23_2021_15_40.sql

SYSTEM echo "Update Delivery Receipt"
source ~/CMS/src/eulap/eb/conf/db/update/update_dr_receiving_details_08_11_2021_10_58.sql
source ~/CMS/src/eulap/eb/conf/db/data/insert_nsb_dr_type_07_02_2021_10_07.sql

SYSTEM echo "Update AR Transaction"
source ~/CMS/src/eulap/eb/conf/db/accounting/ar_service_line.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/transaction_classification.sql
source ~/CMS/src/eulap/eb/conf/db/data/insert_transaction_classification_07_26_2021_19_13.sql
source ~/CMS/src/eulap/eb/conf/db/data/insert_ar_transaction_type_07_27_2021_16_32.sql
source ~/CMS/src/eulap/eb/conf/db/fleet/data/insert_object_type_ar_service_line_07_27_2021_14_22.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_ar_transaction_add_new_columns_07_05_2021_15_05.sql

SYSTEM echo "Update Other Receipt"
source ~/CMS/src/eulap/eb/conf/db/update/update_ar_miscellaneous_add_new_columns_07_15_2021_12_02.sql

SYSTEM echo "Update Repacking"
source ~/CMS/src/eulap/eb/conf/db/update/update_repacking_add_division_id_07_01_2021_12_52.sql

SYSTEM echo "Update Stock Adjustment"
source ~/CMS/src/eulap/eb/conf/db/update/update_stock_in_add_new_columns_08_03_2021_15_50.sql
source ~/CMS/src/eulap/eb/conf/db/data/insert_stock_adjusment_classifications_08_03_2021_17_11.sql

SYSTEM echo "Create Petty Cash Voucher"
source ~/CMS/src/eulap/eb/conf/db/disbursement/petty_cash_voucher.sql
source ~/CMS/src/eulap/eb/conf/db/fleet/data/insert_object_type_petty_cash_voucher_07_30_2021_11_04.sql

SYSTEM echo "Create Supplier Advance Payment Account"
source ~/CMS/src/eulap/eb/conf/db/disbursement/supplier_advance_payment_account.sql

SYSTEM echo "Create Delivery Receipt Account Account"
source ~/CMS/src/eulap/eb/conf/db/disbursement/delivery_receipt_account.sql

SYSTEM echo "Create Customer Advance Payment Account"
source ~/CMS/src/eulap/eb/conf/db/disbursement/customer_advance_payment_account.sql

SYSTEM echo "Insert accounts and account combinations"
source ~/CMS/src/eulap/eb/conf/db/nsb/data/consolidated_insert_accounts_and_account_combi_script_08_11_2021_12_33.sql
