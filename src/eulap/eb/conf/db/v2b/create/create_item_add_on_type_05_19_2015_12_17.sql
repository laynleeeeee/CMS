
-- Description: consolidated create script to create the ITEM_ADD_ON_TYPE and update the ITEM_ADD_ON table.

SYSTEM echo "item_add_on_type.sql";
source ~/eb-fa/src/eulap/eb/conf/db/inv/item_add_on_type.sql

SYSTEM echo "insert_item_add_on_types.sql";
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_item_add_on_types.sql

SYSTEM echo "update_item_add_on_add_on_type_id_05_18_2015_14_45.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_item_add_on_add_on_type_id_05_18_2015_14_45.sql