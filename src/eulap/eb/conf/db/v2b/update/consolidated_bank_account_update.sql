
-- Description: Scripts for bank account update

SYSTEM echo "Create BANK table"
source ~/CMS/src/eulap/eb/conf/db/inv/bank.sql

SYSTEM echo "Insert default banks"
source ~/CMS/src/eulap/eb/conf/db/data/insert_default_banks_03_31_2021_14_29.sql

SYSTEM echo "Update BANK_ACCOUNT table"
source ~/CMS/src/eulap/eb/conf/db/update/update_bank_account_add_bank_id_account_number_03_31_2021_14_40.sql