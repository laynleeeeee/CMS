
-- Description: Consolidated update script for delivery receipt.

source ~/CMS/src/eulap/eb/conf/db/update/update_dr_add_division_id_07_06_2021_16_18.sql

source ~/CMS/src/eulap/eb/conf/db/update/update_dr_receiving_details_08_11_2021_10_58.sql

SOURCE ~/CMS/src/eulap/eb/conf/db/update/update_dr_line_add_percentile_and_description_07_06_2021_13_14.sql

source ~/CMS/src/eulap/eb/conf/db/update/update_dr_witholding_tax_08_05_2021_13_06.sql

source ~/CMS/src/eulap/eb/conf/db/data/insert_nsb_dr_type_07_02_2021_10_07.sql

source ~/CMS/src/eulap/eb/conf/db/revenue/delivery_receipt_line.sql

source ~/CMS/src/eulap/eb/conf/db/disbursement/delivery_receipt_account.sql
