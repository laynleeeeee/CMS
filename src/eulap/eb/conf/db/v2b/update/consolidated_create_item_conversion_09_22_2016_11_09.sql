
-- Description: Consolidated script to create ITEM_CONVERSION and related scripts.

SYSTEM echo "item_conversion.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/item_conversion.sql

SYSTEM echo "item_conversion_line.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/item_conversion_line.sql

SYSTEM echo "Insert Item-Conversion-Relationship OR Type"
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_item_conversion_or_type_09_16_2016_12_00.sql

SYSTEM echo "Insert ITEM_CONVERSION object type"
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_item_conversion_object_type_09_16_2016_11_58.sql