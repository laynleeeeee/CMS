
-- Description: Consolidated script to update Receiving Report to add PO Reference

SYSTEM echo "update_purchase_order_add_eb_object_id_07_08_2016_13_35.sql"
source ~/eb-fa/src/eulap/eb/conf/db/update/update_purchase_order_add_eb_object_id_07_08_2016_13_35.sql

SYSTEM echo "update_purchase_order_item_add_eb_object_id_07_18_2016_12_07.sql"
source ~/eb-fa/src/eulap/eb/conf/db/update/update_purchase_order_item_add_eb_object_id_07_18_2016_12_07.sql

SYSTEM echo "update_receiving_report_item_add_eb_object_id_07_18_2016_12_05.sql"
source ~/eb-fa/src/eulap/eb/conf/db/update/update_receiving_report_item_add_eb_object_id_07_18_2016_12_05.sql

SYSTEM echo "insert_rr_po_object_type_08_23_2016_10_21.sql"
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_rr_po_object_type_08_23_2016_10_21.sql

SYSTEM echo "insert_rr_po_or_type_08_23_2016_10_25.sql"
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_rr_po_or_type_08_23_2016_10_25.sql