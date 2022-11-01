
-- Description	: Consolidated script to integrate taxation updates

SYSTEM echo "============================================="
SYSTEM echo "==========Applying taxation updates=========="
SYSTEM echo "============================================="

SYSTEM echo "wt_account_setting"
source ~/CMS/src/eulap/eb/conf/db/accounting/wt_account_setting.sql

SYSTEM echo "update_wtax_acct_setting_add_creditable_08_25_2020_14_01"
source ~/CMS/src/eulap/eb/conf/db/update/update_wtax_acct_setting_add_creditable_08_25_2020_14_01.sql

SYSTEM echo "tax_type"
source ~/CMS/src/eulap/eb/conf/db/inv/tax_type.sql

SYSTEM echo "insert_tax_type_06_29_2020_11_35"
source ~/CMS/src/eulap/eb/conf/db/data/insert_tax_type_06_29_2020_11_35.sql

SYSTEM echo "insert_default_vat_account_07_01_2020_09_13"
source ~/CMS/src/eulap/eb/conf/db/data/insert_default_vat_account_07_01_2020_09_13.sql

SYSTEM echo "update_receiving_report_item_add_tax_value_07_01_2020_08_44"
source ~/CMS/src/eulap/eb/conf/db/update/update_receiving_report_item_add_tax_value_07_01_2020_08_44.sql

SYSTEM echo "update_cash_sale_item_add_tax_value_07_01_2020_10_57"
source ~/CMS/src/eulap/eb/conf/db/update/update_cash_sale_item_add_tax_value_07_01_2020_10_57.sql

SYSTEM echo "update_cash_sale_ar_line_add_tax_amount_07_02_2020_09_00"
source ~/CMS/src/eulap/eb/conf/db/update/update_cash_sale_ar_line_add_tax_amount_07_02_2020_09_00.sql

SYSTEM echo "update_account_sale_ar_line_add_tax_amount_07_06_2020_06_37"
source ~/CMS/src/eulap/eb/conf/db/update/update_account_sale_ar_line_add_tax_amount_07_06_2020_06_37.sql

SYSTEM echo "update_account_sale_item_add_tax_value_07_06_2020_06_36"
source ~/CMS/src/eulap/eb/conf/db/update/update_account_sale_item_add_tax_value_07_06_2020_06_36.sql

SYSTEM echo "update_cash_sale_return_item_add_tax_value_07_07_2020_14_19"
source ~/CMS/src/eulap/eb/conf/db/update/update_cash_sale_return_item_add_tax_value_07_07_2020_14_19.sql

SYSTEM echo "update_cash_sale_return_ar_line_add_tax_value_07_07_2020_14_24"
source ~/CMS/src/eulap/eb/conf/db/update/update_cash_sale_return_ar_line_add_tax_value_07_07_2020_14_24.sql

SYSTEM echo "update_rts_item_add_tax_type_id_07_10_2020_09_32"
source ~/CMS/src/eulap/eb/conf/db/update/update_rts_item_add_tax_type_id_07_10_2020_09_32.sql

SYSTEM echo "update_cap_item_add_tax_type_id_07_10_2020_09_52"
source ~/CMS/src/eulap/eb/conf/db/update/update_cap_item_add_tax_type_id_07_10_2020_09_52.sql

SYSTEM echo "update_piad_item_add_tax_type_id_07_10_2020_09_53"
source ~/CMS/src/eulap/eb/conf/db/update/update_piad_item_add_tax_type_id_07_10_2020_09_53.sql

SYSTEM echo "update_cap_ar_line_add_tax_type_id_07_23_2020_11_59"
source ~/CMS/src/eulap/eb/conf/db/update/update_cap_ar_line_add_tax_type_id_07_23_2020_11_59.sql

SYSTEM echo "update_cap_delivery_ar_line_add_tax_type_id_07_23_2020_12_09"
source ~/CMS/src/eulap/eb/conf/db/update/update_cap_delivery_ar_line_add_tax_type_id_07_23_2020_12_09.sql

SYSTEM echo "update_ar_transaction_add_withholding_tax_08_06_2020_16_00"
source ~/CMS/src/eulap/eb/conf/db/update/update_ar_transaction_add_withholding_tax_08_06_2020_16_00.sql

SYSTEM echo "update_ap_invoice_line_add_tax_type_id_06_29_2020_11_18"
source ~/CMS/src/eulap/eb/conf/db/update/update_ap_invoice_line_add_tax_type_id_06_29_2020_11_18.sql

SYSTEM echo "update_ap_line_add_tax_type_id_08_07_2020_09_43"
source ~/CMS/src/eulap/eb/conf/db/update/update_ap_line_add_tax_type_id_08_07_2020_09_43.sql

SYSTEM echo "update_ap_invoice_add_withholding_tax_08_12_2020_13_37"
source ~/CMS/src/eulap/eb/conf/db/update/update_ap_invoice_add_withholding_tax_08_12_2020_13_37.sql

SYSTEM echo "update_ar_miscellaneous_line_add_tax_amount_08_12_2020_16_13"
source ~/CMS/src/eulap/eb/conf/db/update/update_ar_miscellaneous_line_add_tax_amount_08_12_2020_16_13.sql

SYSTEM echo "update_ar_miscellaneous_add_withholding_tax_08_12_2020_13_26"
source ~/CMS/src/eulap/eb/conf/db/update/update_ar_miscellaneous_add_withholding_tax_08_12_2020_13_26.sql

SYSTEM echo "update_cash_sales_add_withholding_tax_08_12_2020_09_06"
source ~/CMS/src/eulap/eb/conf/db/update/update_cash_sales_add_withholding_tax_08_12_2020_09_06.sql

SYSTEM echo "update_cash_sales_add_withholding_tax_08_12_2020_09_06"
source ~/CMS/src/eulap/eb/conf/db/update/update_cash_sales_return_add_withholding_tax_08_12_2020_11_12.sql

SYSTEM echo "update_ac_ar_line_add_tax_type_08_13_2020_13_24"
source ~/CMS/src/eulap/eb/conf/db/update/update_ac_ar_line_add_tax_type_08_13_2020_13_24.sql

SYSTEM echo "update_customer_advance_payment_add_withholding_tax_08_13_2020_13_09"
source ~/CMS/src/eulap/eb/conf/db/update/update_customer_advance_payment_add_withholding_tax_08_13_2020_13_09.sql

SYSTEM echo "update_cap_delivery_add_withholding_tax_08_25_2020_15_46"
source ~/CMS/src/eulap/eb/conf/db/update/update_cap_delivery_add_withholding_tax_08_25_2020_15_46.sql

SYSTEM echo "consolidated_insert_withholding_tax_data"
source ~/CMS/src/eulap/eb/conf/db/data/consolidated_insert_withholding_tax_data.sql

SYSTEM echo "========================================="
SYSTEM echo "==========Updating view scripts=========="
SYSTEM echo "========================================="
SYSTEM echo "v_ar_line_analysis"
source ~/CMS/src/eulap/eb/conf/db/view/v_ar_line_analysis.sql
SYSTEM echo "v_ar_receipt_register"
source ~/CMS/src/eulap/eb/conf/db/view/v_ar_receipt_register.sql

SYSTEM echo "======================================="
SYSTEM echo "==========Updating SP scripts=========="
SYSTEM echo "======================================="
SYSTEM echo "sp_get_daily_cash_collection"
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_daily_cash_collection.sql
SYSTEM echo "sp_get_daily_item_sale_detail"
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_daily_item_sale_detail.sql