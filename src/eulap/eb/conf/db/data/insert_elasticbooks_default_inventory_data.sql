
-- Description: Consolidated insert scripts for CMS inventroy default data.

SYSTEM echo "INSERT INVENTORY OBJECT TYPES";
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_inventory_object_types.sql

SYSTEM echo "INSERT INVENTORY OR TYPES";
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_inventory_or_types.sql

source ~/CMS/src/eulap/eb/conf/db/data/insert_serial_item_or_types_12_16_2020_10_37.sql

SYSTEM echo "insert_object_type_for_account_sales"
source ~/CMS/src/eulap/eb/conf/db/payroll/consolidated_insert_object_type_for_account_sales.sql

SYSTEM echo "insert_inventory_invoice_type";
source ~/CMS/src/eulap/eb/conf/db/data/insert_inventory_invoice_type.sql

SYSTEM echo "insert_nsb_inventory_invoice_type";
source ~/CMS/src/eulap/eb/conf/db/data/insert_nsb_invoice_types_06_17_2021_15_14.sql

SYSTEM echo "insert_inventory_ar_transaction_type";
source ~/CMS/src/eulap/eb/conf/db/data/insert_inventory_ar_transaction_type.sql

SYSTEM echo "insert_cash_sale_type_10_07_2015_14_15.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_cash_sale_type_10_07_2015_14_15.sql

SYSTEM echo "insert_customer_advance_payment_type_06_16_2016_16_55.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_customer_advance_payment_type_06_16_2016_16_55.sql

SYSTEM echo "insert_transfer_receipt_type_04_17_2015_10_51.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_transfer_receipt_type_04_17_2015_10_51.sql

SYSTEM echo "insert_repacking_type_05_15_2020_10_24.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_repacking_type_05_15_2020_10_24.sql

-- ITEM ADD ON TYPE
source ~/CMS/src/eulap/eb/conf/db/data/insert_item_add_on_types.sql

-- ITEM DISCOUNT TYPE
source ~/CMS/src/eulap/eb/conf/db/inv/insert_item_discount_type.sql

-- UNIT_MEASUREMENT
-- SYSTEM echo 'Inserting UNIT_MEASUREMENT';
-- source ~/CMS/src/eulap/eb/conf/db/data/insert_default_unit_measurement.sql

-- INVENTORY_ACCOUNT
SYSTEM echo 'Inserting INVENTORY_ACCOUNT';
INSERT INTO `INVENTORY_ACCOUNT` VALUES (1, 1, 1, 1, 1, 1, NOW(), 1, NOW());

SYSTEM echo "insert_stock_adjusment_classifications_02_20_2018_16_33";
source ~/CMS/src/eulap/eb/conf/db/data/insert_stock_adjusment_classifications_02_20_2018_16_33.sql
