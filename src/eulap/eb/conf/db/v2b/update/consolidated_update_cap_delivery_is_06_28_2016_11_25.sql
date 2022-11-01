
-- Description: Consolidated script to update CAP_DELIVERY to integrate Individual Selection

SYSTEM echo "update_cap_delivery_add_customer_advance_payment_type_id.sql"
source ~/eb-fa/src/eulap/eb/conf/db/update/update_cap_delivery_add_customer_advance_payment_type_id_06_28_2016_11_14.sql

SYSTEM echo "update_cap_delivery_add_eb_object_id.sql"
source ~/eb-fa/src/eulap/eb/conf/db/update/update_cap_delivery_add_eb_object_id_06_28_2016_11_55.sql

SYSTEM echo "Insert Paid-in-Advance-Delivery-Relationship OR Type"
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_cap_delivery_or_type_06_28_2016_11_48.sql

SYSTEM echo "Insert PAID_IN_ADVANCE_DELIVERY object type"
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_cap_delivery_object_type_06_28_2016_11_45.sql