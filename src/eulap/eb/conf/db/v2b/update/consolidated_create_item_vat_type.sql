
-- Description: Scripts for sales order update

SYSTEM echo "Create ITEM VAT TYPE."
source ~/CMS/src/eulap/eb/conf/db/inv/item_vat_type.sql

SYSTEM echo "Insertint ITEM VAT Data."
source ~/CMS/src/eulap/eb/conf/db/data/insert_default_item_vat_types.sql

SYSTEM echo "Add BARCODE, WAREHOUSE_ID, and ITEM_VAT_TYPE_ID in ITEM table."
source ~/CMS/src/eulap/eb/conf/db/update/update_item_add_columns_05_25_2021_16_35.sql