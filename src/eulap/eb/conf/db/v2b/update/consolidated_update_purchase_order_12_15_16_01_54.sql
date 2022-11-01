
-- Description: Consolidated update scripts for purchase order to add unit cost.

SYSTEM echo "Inserting object types"
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_rr_po_object_type_08_23_2016_10_21.sql

SYSTEM echo "Update R_PURCHASE_ORDER add EB_OBJECT"
source ~/eb-fa/src/eulap/eb/conf/db/update/update_purchase_order_add_eb_object_id_07_08_2016_13_35.sql

SYSTEM echo "Update R_PURCHASE_ORDER_ITEM add EB_OBJECT"
source ~/eb-fa/src/eulap/eb/conf/db/update/update_purchase_order_item_add_eb_object_id_07_18_2016_12_07.sql

SYSTEM echo "Add column UNIT_COST in R_PURCHASE_ORDER_ITEM"
source ~/eb-fa/src/eulap/eb/conf/db/update/update_purchase_order_add_column_unit_cost_11_16_2016.sql