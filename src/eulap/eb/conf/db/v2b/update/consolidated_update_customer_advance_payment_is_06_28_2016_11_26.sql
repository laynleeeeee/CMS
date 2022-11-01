
-- Description: Consolidated script to update CUSTOMER_ADVANCE_PAYMENT to integrate Individual Selection

SYSTEM echo "customer_advance_payment_type.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/customer_advance_payment_type.sql

SYSTEM echo "insert_customer_advance_payment_type.sql"
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_customer_advance_payment_type_06_16_2016_16_55.sql

SYSTEM echo "update_cap_add_customer_advance_payment_type_id.sql"
source ~/eb-fa/src/eulap/eb/conf/db/update/update_cap_add_customer_advance_payment_type_id_06_16_2016_17_07.sql

SYSTEM echo "update_customer_advance_payment_add_eb_object_id.sql"
source ~/eb-fa/src/eulap/eb/conf/db/update/update_customer_advance_payment_add_eb_object_id_06_16_2016_17_18.sql

SYSTEM echo "Insert Customer-Advance-Payment-Relationship OR Type"
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_cap_or_type_06_28_2016_11_39.sql

SYSTEM echo "Insert CUSTOMER_ADVANCE_PAYMENT object type"
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_cap_object_type_06_28_2016_11_37.sql