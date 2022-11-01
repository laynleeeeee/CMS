
-- Description: Create script for accounting modules.

SYSTEM echo "=============================="
SYSTEM echo "==========ACCOUNTING=========="
SYSTEM echo "=============================="

SYSTEM echo "Account"
source ~/CMS/src/eulap/eb/conf/db/accounting/normal_balance.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/account_type.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/account.sql

SYSTEM echo "Account Combination"
source ~/CMS/src/eulap/eb/conf/db/accounting/account_combination.sql

SYSTEM echo "AP Line Setup"
source ~/CMS/src/eulap/eb/conf/db/accounting/ap_line_setup.sql

SYSTEM echo "AR Line Setup"
source ~/CMS/src/eulap/eb/conf/db/accounting/ar_line_setup.sql

SYSTEM echo "Bank Account"
source ~/CMS/src/eulap/eb/conf/db/inv/bank.sql
source ~/CMS/src/eulap/eb/conf/db/data/insert_default_banks_03_31_2021_14_29.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/bank_account.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_bank_account_add_bank_id_account_number_03_31_2021_14_40.sql

SYSTEM echo "Checkbook"
source ~/CMS/src/eulap/eb/conf/db/accounting/checkbook_template.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/checkbook.sql

SYSTEM echo "Term"
source ~/CMS/src/eulap/eb/conf/db/accounting/term.sql

SYSTEM echo "Business Classification"
source ~/CMS/src/eulap/eb/conf/db/inv/business_classification.sql
source ~/CMS/src/eulap/eb/conf/db/data/insert_default_business_classification.sql

SYSTEM echo "Customer and Customer Account"
source ~/CMS/src/eulap/eb/conf/db/accounting/ar_customer.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_ar_customer_add_business_classification_06_10_2021_10_51.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/ar_customer_account.sql

SYSTEM echo "Receipt Method"
source ~/CMS/src/eulap/eb/conf/db/accounting/receipt_method.sql

SYSTEM echo "Supplier and Supplier Account"
source ~/CMS/src/eulap/eb/conf/db/accounting/bus_reg_type.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/supplier.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/supplier_account.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_supplier_add_business_classification_06_10_2021_15_43.sql

SYSTEM echo "Time Period"
source ~/CMS/src/eulap/eb/conf/db/accounting/time_period_status.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/time_period.sql

SYSTEM echo "Tax type"
source ~/CMS/src/eulap/eb/conf/db/inv/tax_type.sql
source ~/CMS/src/eulap/eb/conf/db/data/insert_tax_type_06_29_2020_11_35.sql

SYSTEM echo "wt_account_setting"
source ~/CMS/src/eulap/eb/conf/db/accounting/wt_account_setting.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_wtax_acct_setting_add_creditable_08_25_2020_14_01.sql

SYSTEM echo "currency"
source ~/CMS/src/eulap/eb/conf/db/accounting/currency.sql

SYSTEM echo "currency_rate"
source ~/CMS/src/eulap/eb/conf/db/accounting/currency_rate.sql
source ~/CMS/src/eulap/eb/conf/db/data/insert_currency_default_data_06_10_2021_09_52.sql

SYSTEM echo "custodian_account"
source ~/CMS/src/eulap/eb/conf/db/accounting/custodian_account.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/user_custodian.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/user_custodian_lines.sql

SYSTEM echo "sales_personnel"
source ~/CMS/src/eulap/eb/conf/db/accounting/sales_personnel.sql

SYSTEM echo "service_setting"
source ~/CMS/src/eulap/eb/conf/db/accounting/service_setting.sql

SYSTEM echo "===================================="
SYSTEM echo "==========ACCOUNTING FORMS=========="
SYSTEM echo "===================================="

SYSTEM echo "AP Invoice"
source ~/CMS/src/eulap/eb/conf/db/accounting/invoice_classification.sql
source ~/CMS/src/eulap/eb/conf/db/data/insert_invoice_classification_06_17_2021_14_02.sql

source ~/CMS/src/eulap/eb/conf/db/accounting/invoice_type.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/ap_invoice.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/ap_line.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/ap_invoice_item.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_ap_invoice_add_new_columns_06_15_2021_20_47.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_ap_line_add_tax_type_id_08_07_2020_09_43.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_ap_invoice_add_withholding_tax_08_12_2020_13_37.sql

SYSTEM echo "Payment Type"
source ~/CMS/src/eulap/eb/conf/db/accounting/payment_type.sql

SYSTEM echo "AP Payment"
source ~/CMS/src/eulap/eb/conf/db/accounting/ap_payment.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/ap_payment_invoice.sql

SYSTEM echo "AR Transaction"
source ~/CMS/src/eulap/eb/conf/db/accounting/ar_transaction_type.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/ar_transaction.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/ar_line.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_ar_transaction_add_withholding_tax_08_06_2020_16_00.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/ar_service_line.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/transaction_classification.sql

SYSTEM echo "AR Receipt"
source ~/CMS/src/eulap/eb/conf/db/accounting/ar_receipt_type.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/ar_receipt.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/ar_receipt_transaction.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/ac_ar_line.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_ac_ar_line_add_tax_type_08_13_2020_13_24.sql

SYSTEM echo "AR Miscellaneous"
source ~/CMS/src/eulap/eb/conf/db/accounting/ar_miscellaneous_type.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/ar_miscellaneous.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/ar_miscellaneous_line.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_ar_miscellaneous_add_withholding_tax_08_12_2020_13_26.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_ar_miscellaneous_line_add_tax_amount_08_12_2020_16_13.sql

SYSTEM echo "General Ledger"
source ~/CMS/src/eulap/eb/conf/db/accounting/gl_entry_source.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/gl_status.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/general_ledger.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/gl_entry.sql

SYSTEM echo "==========================================="
SYSTEM echo "==========ACCOUNTING DEFAULT DATA=========="
SYSTEM echo "==========================================="
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_default_accounting_data.sql
