
-- Description: Consolidated script to create POS_MIDDLEWARE_SETTING and CONSOLIDATED POS

SYSTEM echo "Create REFERENCE_DOCUMENT"
source ~/eb-fa/src/eulap/eb/conf/db/inv/reference_document.sql

SYSTEM echo "insert object type for REFERENCE_TYPE"
INSERT INTO OBJECT_TYPE VALUES (30, 'REFERENCE_DOCUMENT', 'REFERENCE_DOCUMENT', 1, 1, NOW(), 1, NOW());

SYSTEM echo "Modify cash sale type"
source ~/eb-fa/src/eulap/eb/conf/db/update/update_cash_sale_type_modify_name_04_07_2016_17_13.sql

SYSTEM echo "Prepare CUSTOMER TYPE"
source ~/eb-fa/src/eulap/eb/conf/db/accounting/customer_type.sql

SYSTEM echo "Insert default CUSTOMER TYPE"
INSERT INTO CUSTOMER_TYPE VALUES (1, 'Regular', 'Regular', 1, 1, NOW(), 1, NOW());

SYSTEM echo "Prepare POS_MIDDLEWARE_SETTING"
source ~/eb-fa/src/eulap/eb/conf/db/v2b/update/consolidated_create_pos_middleware_setting.sql

SYSTEM echo "Update Ar Customer to add customer type"
source ~/eb-fa/src/eulap/eb/conf/db/update/update_ar_customer_add_customer_type_03_04_16_09_58.sql

SYSTEM echo "Prepare Consolidated POS"
source ~/eb-fa/src/eulap/eb/conf/db/v2b/update/consolidated_create_consolidated_pos.sql

SYSTEM echo "customer_advance_payment_type.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/customer_advance_payment_type.sql
SYSTEM echo "update_cap_add_customer_advance_payment_type_id_06_16_2016_17_07.sql"
source ~/eb-fa/src/eulap/eb/conf/db/update/update_cap_add_customer_advance_payment_type_id_06_16_2016_17_07.sql
SYSTEM echo "update_cap_delivery_add_customer_advance_payment_type_id_06_28_2016_11_14.sql"
source ~/eb-fa/src/eulap/eb/conf/db/update/update_cap_delivery_add_customer_advance_payment_type_id_06_28_2016_11_14.sql
SYSTEM echo "cash_sale_return_ar_line.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/cash_sale_return_ar_line.sql

SYSTEM echo "Update DAILY_CASH_COLLECTION"
source ~/eb-fa/src/eulap/eb/conf/db/sp/sp_get_daily_cash_collection.sql

SYSTEM echo "Update ACCOUNT_ANALYSIS"
source ~/eb-fa/src/eulap/eb/conf/db/sp/sp_get_account_analysis.sql

SYSTEM echo "Update ACCOUNT_BALANCE"
source ~/eb-fa/src/eulap/eb/conf/db/sp/sp_get_account_balance.sql

SYSTEM echo "Update DAILY_ITEM_SALE_DETAIL"
source ~/eb-fa/src/eulap/eb/conf/db/sp/sp_get_daily_item_sale_detail.sql

SYSTEM echo "Update GROSS_PROFIT_ANALYSIS"
source ~/eb-fa/src/eulap/eb/conf/db/sp/sp_get_gross_profit_analysis.sql

SYSTEM echo "Update AR_RECEIPT_REGISTER"
source ~/eb-fa/src/eulap/eb/conf/db/view/v_ar_receipt_register.sql

SYSTEM echo "Update ITEM_SALES_CUSTOMER"
source ~/eb-fa/src/eulap/eb/conf/db/view/v_item_sales_customer.sql

SYSTEM echo "Update V_JOURNAL_ENTRY"
source ~/eb-fa/src/eulap/eb/conf/db/view/v_journal_entry.sql