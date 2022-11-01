
-- Description: Create script for account sales PO.

SYSTEM echo "account_sales"
source ~/CMS/src/eulap/eb/conf/db/inv/account_sales.sql
SYSTEM echo "account_sales_po_item"
source ~/CMS/src/eulap/eb/conf/db/inv/account_sales_po_item.sql
SYSTEM echo "consolidated_insert_object_type_for_account_sales"
source ~/CMS/src/eulap/eb/conf/db/payroll/consolidated_insert_object_type_for_account_sales.sql
