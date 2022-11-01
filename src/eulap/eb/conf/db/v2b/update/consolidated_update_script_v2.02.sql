
-- Description: Consolidated update script for v 2.02.

-- CREATE - TABLES
SYSTEM echo "consolidated_create_script_v2.sql";
source ~/eb-fa/src/eulap/eb/conf/db/v2b/create/consolidated_create_script_v2.02.sql

-- UPDATE - TABLES
SYSTEM echo "update_ap_invoice_drop_ap_invoice_status_id_03_17_15_08_57.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_ap_invoice_drop_ap_invoice_status_id_03_17_15_08_57.sql

SYSTEM echo "update_ap_payment_drop_ap_payment_status_id_03_16_15.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_ap_payment_drop_ap_payment_status_id_03_16_15.sql

SYSTEM echo "update_ap_invoice_drop_deleted_03_19_2015_10_05.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_ap_invoice_drop_deleted_03_19_2015_10_05.sql

SYSTEM echo "update_ap_invoice_add_eb_object_id_03_31_2015_10_55.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_ap_invoice_add_eb_object_id_03_31_2015_10_55.sql

SYSTEM echo "update_rm_item_add_eb_object_id_03_31_2015_11_52.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_rm_item_add_eb_object_id_03_31_2015_11_52.sql

SYSTEM echo "update_ap_invoice_line_add_eb_object_id_03_31_2015_11_57.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_ap_invoice_line_add_eb_object_id_03_31_2015_11_57.sql

SYSTEM echo "update_stock_adjustment_add_eb_object_id_04_07_2015_11_18.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_stock_adjustment_add_eb_object_id_04_07_2015_11_18.sql

SYSTEM echo "update_sa_item_add_eb_object_id_04_07_2015_11_20.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_sa_item_add_eb_object_id_04_07_2015_11_20.sql

SYSTEM echo "update_account_sale_item_add_eb_object_id_04_07_2015_13_40.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_account_sale_item_add_eb_object_id_04_07_2015_13_40.sql

SYSTEM echo "update_ar_transaction_add_eb_object_id_04_07_2015_13_41.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_ar_transaction_add_eb_object_id_04_07_2015_13_41.sql

SYSTEM echo "update_ar_line_add_eb_object_id_04_08_2015_18_09.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_ar_line_add_eb_object_id_04_08_2015_18_09.sql

SYSTEM echo "update_cash_sale_return_add_eb_object_id_04_15_2015_13_32.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_cash_sale_return_add_eb_object_id_04_15_2015_13_32.sql

SYSTEM echo "update_ap_payment_add_column_payee_and_specify_payee_01_15_16_10_43.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_ap_payment_add_column_payee_and_specify_payee_01_15_16_10_43.sql

SYSTEM echo "update_supplier_modify_name_03_07_2016_16_07.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_supplier_modify_name_03_07_2016_16_07.sql

-- UPDATE - INSERT DATA
SYSTEM echo "insert_or_type.sql";
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_or_type.sql

SYSTEM echo "insert_object_type.sql";
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_object_type.sql

SYSTEM echo "insert_invoice_type_rr_raw_material_03_25_2015_13_42.sql";
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_invoice_type_rr_raw_material_03_25_2015_13_42.sql

SYSTEM echo "insert_cash_sale_type_item_selection_04_07_2015_15_17.sql";
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_cash_sale_type_item_selection_04_07_2015_15_17.sql

SYSTEM echo "insert_transfer_receipt_type_04_17_2015_10_51.sql";
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_transfer_receipt_type_04_17_2015_10_51.sql

SYSTEM echo "update_cash_sale_and_cash_sale_item_add_eb_object_id_04_08_2015_15_00.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_cash_sale_and_cash_sale_item_add_eb_object_id_04_08_2015_15_00.sql

SYSTEM echo "update_transfer_receipt_item_add_eb_object_id_04_16_2015_17_42.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_transfer_receipt_item_add_eb_object_id_04_16_2015_17_42.sql

SYSTEM echo "update_transfer_receipt_add_transfer_receipt_type_id_04_15_2015_10_50.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_transfer_receipt_add_transfer_receipt_type_id_04_15_2015_10_50.sql

SYSTEM echo "update_transfer_receipt_add_eb_object_id_04_15_2015_11_01.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_transfer_receipt_add_eb_object_id_04_15_2015_11_01.sql

SYSTEM echo "update_item_add_on_add_on_type_id_05_18_2015_14_45.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_item_add_on_add_on_type_id_05_18_2015_14_45.sql

SYSTEM echo "insert_ar_transaction_type_acct_sale_individual_selection_04_07_2015_13_21.sql";
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_ar_transaction_type_acct_sale_individual_selection_04_07_2015_13_21.sql

SYSTEM echo "update_ar_customer_add_customer_type_03_04_16_09_58.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_ar_customer_add_customer_type_03_04_16_09_58.sql

SYSTEM echo "insert_default_customer_type_03_03_2016_15_43.sql";
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_default_customer_type_03_03_2016_15_43.sql

SYSTEM echo "update_checkbook_add_checkbook_template_12_09_15_11_55.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_checkbook_add_checkbook_template_12_09_15_11_55.sql

SYSTEM echo "insert_checkbook_template_12_09_2015_13_49.sql";
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_checkbook_template_12_09_2015_13_49.sql

SYSTEM echo "update_check_template_add_max_char_name_03_09_2016_15_25.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_check_template_add_max_char_name_03_09_2016_15_25.sql

SYSTEM echo "update_ar_customer_add_customer_type_03_04_16_09_58.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_ar_customer_add_customer_type_03_04_16_09_58.sql

SYSTEM echo "insert_form_status_encoded_03_30_2016_14_38.sql";
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_form_status_encoded_03_30_2016_14_38.sql

SYSTEM echo "cash_sale_return_ar_line.sql";
source ~/eb-fa/src/eulap/eb/conf/db/inv/cash_sale_return_ar_line.sql

SYSTEM echo "insert_employee_status_04_27_2016_09_48.sql";
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_employee_status_04_27_2016_09_48.sql

SYSTEM echo "update_alter_table_employee_shift_drop_column_weekly_working_days_05_16_2016_12_20.sql";
source ~/eb-fa/src/eulap/eb/conf/db/v2b/update/update_alter_table_employee_shift_drop_column_weekly_working_days_05_16_2016_12_20.sql

-----======================== PLEASE ADD THE UPDATE SCRIPTS FOR TABLES BEFORE THIS LINE ========================-----

-- CREATE or UPDATE - VIEWS
SYSTEM echo "=======================================================================";
SYSTEM echo "=======================Updating all of the views=======================";
SYSTEM echo "=======================================================================";
source ~/eb-fa/src/eulap/eb/conf/db/view/consolidated_view_script.sql

-- CREATE or UPDATE - STORED PROCEDURES
SYSTEM echo "=======================================================================";
SYSTEM echo "==============Updating all of the stored procedures====================";
SYSTEM echo "=======================================================================";
source ~/eb-fa/src/eulap/eb/conf/db/sp/consolidated_stored_procedure_script.sql
