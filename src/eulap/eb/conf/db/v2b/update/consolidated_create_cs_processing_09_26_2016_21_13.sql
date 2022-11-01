
-- Description: Consolidated script for CS - Processing form.

SYSTEM echo "inserting object types"
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_csi_rm_fp_object_type_09_24_2016_11_35.sql

SYSTEM echo "inserting or type"
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_cs_processing_or_type_09_26_2016_10_29.sql

SYSTEM echo "csi_finished_product.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/csi_finished_product.sql

SYSTEM echo "csi_raw_material.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/csi_raw_material.sql

SYSTEM echo "insert Cash Sales - Processing type"
INSERT INTO CASH_SALE_TYPE VALUES (6, 'Cash Sales - Processing', 1, 1 , NOW(), 1, NOW());