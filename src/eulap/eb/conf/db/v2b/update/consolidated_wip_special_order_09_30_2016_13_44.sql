
-- Description: Consolidated script to create WIP_SPECIAL_ORDER and related scripts.

SYSTEM echo "wip_special_order.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/wip_special_order.sql

SYSTEM echo "wipso_material.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/wipso_material.sql

SYSTEM echo "wipso_other_charge.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/wipso_other_charge.sql

SYSTEM echo "wipso_finished_product.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/wipso_finished_product.sql

SYSTEM echo "insert_wip_special_order_or_type_09_27_2016_13_14.sql"
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_wip_special_order_or_type_09_27_2016_13_14.sql

SYSTEM echo "insert_wip_special_order_object_type_09_24_2016_10_10.sql"
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_wip_special_order_object_type_09_24_2016_10_10.sql

SYSTEM echo "v_journal_entry.sql"
source ~/eb-fa/src/eulap/eb/conf/db/view/v_journal_entry.sql

SYSTEM echo "sp_get_account_analysis.sql"
source ~/eb-fa/src/eulap/eb/conf/db/sp/sp_get_account_analysis.sql

SYSTEM echo "sp_get_account_balance.sql"
source ~/eb-fa/src/eulap/eb/conf/db/sp/sp_get_account_balance.sql

SYSTEM echo "sp_get_beginning_balance.sql"
source ~/eb-fa/src/eulap/eb/conf/db/sp/sp_get_beginning_balance.sql
