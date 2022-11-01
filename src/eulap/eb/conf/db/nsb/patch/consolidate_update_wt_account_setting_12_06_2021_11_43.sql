
-- Description	: Consolidated script to set BIR ATC

source ~/CMS/src/eulap/eb/conf/db/accounting/bir_atc.sql

source ~/CMS/src/eulap/eb/conf/db/data/insert_atc_default_data_12_02_2021_11_59.sql

source ~/CMS/src/eulap/eb/conf/db/nsb/data/insert_ewt_180_wt_tax_account_12_06_2021_11_21.sql

source ~/CMS/src/eulap/eb/conf/db/nsb/data/insert_ewt_230_wt_tax_account_12_06_2021_11_26.sql

source ~/CMS/src/eulap/eb/conf/db/update/update_wt_account_setting_add_atc_id_12_06_2021_11_17.sql

source ~/CMS/src/eulap/eb/conf/db/update/update_wt_account_setting_add_wt_type_id_12_06_2021_11_18.sql

source ~/CMS/src/eulap/eb/conf/db/data/update_wt_acct_seeting_set_atc_value_12_06_2021_11_41.sql