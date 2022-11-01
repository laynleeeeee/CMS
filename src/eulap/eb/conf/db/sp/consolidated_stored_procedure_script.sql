
-- Description: Consolidated SP(Stored Procedures) script. All SP should be added in this file.

SYSTEM echo "Running consolidated stored procedures";

source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_user_id_by_username.sql
SYSTEM echo "sp_get_user_id_by_username.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_check_has_duplicate.sql
SYSTEM echo "sp_check_has_duplicate.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_account_analysis.sql
SYSTEM echo "sp_get_account_analysis.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_account_balance.sql
SYSTEM echo "sp_get_account_balance.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_account_balances.sql
SYSTEM echo "sp_get_account_balances.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_all_accounts.sql
SYSTEM echo "sp_get_all_accounts.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_balance_sheet_contra.sql
SYSTEM echo "sp_get_balance_sheet_contra.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_balance_sheet.sql
SYSTEM echo "sp_get_balance_sheet.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_bank_reconciliation.sql
SYSTEM echo "sp_get_bank_reconciliation.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_beginning_balance.sql
SYSTEM echo "sp_get_beginning_balance.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_contra_account.sql
SYSTEM echo "sp_get_contra_account.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_gross_profit_analysis.sql
SYSTEM echo "sp_get_gross_profit_analysis.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_income_statement.sql
SYSTEM echo "sp_get_income_statement.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_journal_entries_register.sql
SYSTEM echo "sp_get_journal_entries_register.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_daily_cash_collection.sql
SYSTEM echo "sp_get_daily_cash_collection.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_daily_item_sale_detail.sql
SYSTEM echo "sp_get_daily_item_sale_detail.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_daily_item_sales.sql
SYSTEM echo "sp_get_daily_item_sales.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_item_existing_stocks.sql
SYSTEM echo "sp_get_item_existing_stocks.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_book_balance.sql
SYSTEM echo "sp_get_book_balance.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_invoice_aging.sql
SYSTEM echo "sp_get_invoice_aging.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_transaction_aging.sql
SYSTEM echo "sp_get_transaction_aging.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_unposted_forms.sql
SYSTEM echo "sp_get_unposted_forms.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_forms_by_ref_object_id.sql
SYSTEM echo "sp_get_forms_by_ref_object_id.sql";
source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_customer_available_balance.sql
SYSTEM echo "sp_get_customer_available_balance.sql";