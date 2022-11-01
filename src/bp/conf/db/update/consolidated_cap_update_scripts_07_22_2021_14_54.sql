
-- Description: Consolidated update script for customer advance payment.

source ~/CMS/src/eulap/eb/conf/db/revenue/update/update_cap_add_division_and_currency_07_19_2021_14_03.sql

source ~/CMS/src/eulap/eb/conf/db/data/insert_nsb_cap_type_07_19_2021_13_22.sql

source ~/CMS/src/eulap/eb/conf/db/disbursement/customer_advance_payment_line.sql

source ~/CMS/src/eulap/eb/conf/db/disbursement/customer_advance_payment_account.sql
