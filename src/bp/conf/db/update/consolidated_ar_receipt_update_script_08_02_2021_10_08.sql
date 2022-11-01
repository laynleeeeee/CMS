
-- Description: Consolidated update script for receiving report.

source ~/CMS/src/eulap/eb/conf/db/revenue/update/update_ar_receipt_add_division_and_currency_07_22_2021_09_44.sql

source ~/CMS/src/eulap/eb/conf/db/accounting/ar_receipt_line_type.sql

source ~/CMS/src/eulap/eb/conf/db/disbursement/ar_receipt_line.sql

source ~/CMS/src/eulap/eb/conf/db/data/insert_default_ar_receipt_line_type.sql