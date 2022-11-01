
-- Description: Consolidated update script for ar invoice.

source ~/CMS/src/eulap/eb/conf/db/revenue/data/insert_ar_invoice_nsb_type_07_08_2021_08_58.sql

source ~/CMS/src/eulap/eb/conf/db/update/update_ari_add_receving_details_08_04_2021_13_30.sql

source ~/CMS/src/eulap/eb/conf/db/disbursement/ar_invoice_account.sql

source ~/CMS/src/eulap/eb/conf/db/revenue/ar_invoice_line.sql