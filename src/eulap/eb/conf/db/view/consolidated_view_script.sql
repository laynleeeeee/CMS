
-- Description: Consolidated view script. All view should be added in this file.

SYSTEM echo "Running consolidated view scripts";
source ~/CMS/src/eulap/eb/conf/db/view/v_ar_line_analysis.sql
SYSTEM echo "v_ar_line_analysis.sql";
source ~/CMS/src/eulap/eb/conf/db/view/v_ar_receipt_register.sql
SYSTEM echo "v_ar_receipt_register.sql";
source ~/CMS/src/eulap/eb/conf/db/view/v_customer_acct_history.sql
SYSTEM echo "v_customer_acct_history.sql";
source ~/CMS/src/eulap/eb/conf/db/view/v_invoice_history.sql
SYSTEM echo "v_invoice_history.sql";
source ~/CMS/src/eulap/eb/conf/db/view/v_item_history.sql
SYSTEM echo "v_item_history.sql";
source ~/CMS/src/eulap/eb/conf/db/view/v_item_sales_customer.sql
SYSTEM echo "v_item_sales_customer.sql";
source ~/CMS/src/eulap/eb/conf/db/view/v_journal_entry.sql
SYSTEM echo "v_journal_entry.sql";
source ~/CMS/src/eulap/eb/conf/db/view/v_statement_of_account.sql
SYSTEM echo "v_statement_of_account.sql";
source ~/CMS/src/eulap/eb/conf/db/view/v_transaction_history.sql
SYSTEM echo "v_transaction_history.sql";