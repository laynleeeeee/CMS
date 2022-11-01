
-- Description: Consolidated script to create POS_MIDDLEWARE_SETTING

SYSTEM echo "Create POS_MIDDLEWARE_SETTING"
source ~/eb-fa/src/eulap/eb/conf/db/inv/pos_middleware_setting.sql

SYSTEM echo "Create CASH_COUNT_DENOMINATION"
source ~/eb-fa/src/eulap/eb/conf/db/inv/cash_count_denomination.sql

SYSTEM echo "Insert CASH_COUNT_DENOMINATION"
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_cash_count_denomination_06_21_2016_13_29.sql