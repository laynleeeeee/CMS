
-- Description: Consolidated script for NSB related update script. 

-- AP Invoice Non PO Form
source ~/CMS/src/eulap/eb/conf/db/update/update_ap_invoice_add_new_columns_06_15_2021_20_47.sql

-- Other Receipt Form
source ~/CMS/src/eulap/eb/conf/db/update/update_ar_miscellaneous_add_new_columns_07_15_2021_12_02.sql 

-- AR Transaction Form
source ~/CMS/src/eulap/eb/conf/db/update/update_ar_transaction_add_new_columns_07_05_2021_15_05.sql 

-- Sales Order Form
source ~/CMS/src/eulap/eb/conf/db/update/update_sales_order_add_division_id_currency_id_po_no_06_23_2021_15_48.sql 

-- Reclass/Repacking Form
source ~/CMS/src/eulap/eb/conf/db/update/update_repacking_add_division_id_07_01_2021_12_52.sql 

-- Stock Adjustment In Form
source ~/CMS/src/eulap/eb/conf/db/update/update_stock_in_add_new_columns_08_03_2021_15_50.sql

-- insert Invoice type for ap invoice confidential
source ~/CMS/src/eulap/eb/conf/db/update/nsb/insert_invoice_type_ap_invoice_conf_08_10_2021_16_35.sql

-- Purchase Order Form
source ~/CMS/src/eulap/eb/conf/db/update/update_po_item_add_tax_type_id_06_09_2021_14_03.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_po_line_add_tax_type_id_06_09_2021_14_4.sql

-- Receiving Report Form
source ~/CMS/src/bp/conf/db/update/consolidated_rr_update_script_07_07_2021_20_39.sql

-- Reference Document
source ~/CMS/src/eulap/eb/conf/db/update/update_reference_document_add_file_path_05_26_2021_10_28.sql

-- Supplier Advance Payment
source ~/CMS/src/eulap/eb/conf/db/disbursement/supplier_advance_payment.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/supplier_advance_payment_line.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/supplier_advance_payment_account.sql

-- AP Payment Form
source ~/CMS/src/bp/conf/db/update/consolidated_ap_payment_update_script_07_07_2021_20_40.sql

-- Delivery Receipt Form
source ~/CMS/src/bp/conf/db/update/consolidated_dr_update_script_08_04_2021_13_09.sql

-- Customer Advance Payment Form
source ~/CMS/src/bp/conf/db/update/consolidated_cap_update_scripts_07_22_2021_14_54.sql

-- AR Invoice Form
source ~/CMS/src/bp/conf/db/update/consolidated_ar_invoice_update_script_08_06_2021_20_23.sql

-- Account Collection Form
source ~/CMS/src/bp/conf/db/update/consolidated_ar_receipt_update_script_08_02_2021_10_08.sql

-- Stored Procedures
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_ref_form_serial_number.sql
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_item_existing_stocks.sql
