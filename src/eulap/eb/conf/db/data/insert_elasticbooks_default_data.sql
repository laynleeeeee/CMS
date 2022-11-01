
-- Description: Consolidated insert scripts for default data for Elasticbooks clean DB.

source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_default_base_data.sql

-- GL_ENTRY_SOURCE
SYSTEM echo 'Inserting GL_ENTRY_SOURCE';
INSERT INTO `GL_ENTRY_SOURCE` VALUES (1,'General Ledger');

-- GL_STATUS
source ~/CMS/src/eulap/eb/conf/db/data/insert_gl_status.sql

-- BUS_REG_TYPE
source ~/CMS/src/eulap/eb/conf/db/data/insert_business_registration_type.sql

-- NORMAL_BALANCE
source ~/CMS/src/eulap/eb/conf/db/data/insert_normal_balance.sql

-- TIME_PERIOD_STATUS
source ~/CMS/src/eulap/eb/conf/db/data/insert_time_period_status.sql

-- OBJECT_TYPE
source ~/CMS/src/eulap/eb/conf/db/data/insert_object_type.sql

-- OR_TYPE
source ~/CMS/src/eulap/eb/conf/db/data/insert_or_type.sql

SYSTEM echo "insert_form_status_10_07_2015_14_15.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_form_status_10_07_2015_14_15.sql

SYSTEM echo "insert_invoice_type_10_07_2015_14_14.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_invoice_type_10_07_2015_14_14.sql

SYSTEM echo "insert_ar_transaction_type_10_07_2015_14_15.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_ar_transaction_type_10_07_2015_14_15.sql

SYSTEM echo "insert_ar_miscellaneous_type_10_28_2015_16_37.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_ar_miscellaneous_type_10_28_2015_16_37.sql

SYSTEM echo "insert_ar_receipt_type_10_07_2015_14_15.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_ar_receipt_type_10_07_2015_14_15.sql

SYSTEM echo "insert_cash_sale_type_10_07_2015_14_15.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_cash_sale_type_10_07_2015_14_15.sql

SYSTEM echo "insert_customer_advance_payment_type_06_16_2016_16_55.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_customer_advance_payment_type_06_16_2016_16_55.sql

SYSTEM echo "insert_processing_report_type_08_19_2015_15_45.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_processing_report_type_08_19_2015_15_45.sql

SYSTEM echo "insert_transfer_receipt_type_04_17_2015_10_51.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_transfer_receipt_type_04_17_2015_10_51.sql

-- ITEM ADD ON TYPE
source ~/CMS/src/eulap/eb/conf/db/data/insert_item_add_on_types.sql

-- ITEM DISCOUNT TYPE
source ~/CMS/src/eulap/eb/conf/db/inv/insert_item_discount_type.sql

SYSTEM echo "insert_account_type_10_07_2015_14_33.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_account_type_10_07_2015_14_33.sql

SYSTEM echo "insert_account_combination_10_07_2015_14_35.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_account_combination_10_07_2015_14_35.sql

SYSTEM echo "insert_checkbook_template_12_09_2015_13_49.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_checkbook_template_12_09_2015_13_49.sql

-- UNIT_MEASUREMENT
SYSTEM echo 'Inserting UNIT_MEASUREMENT';
INSERT INTO `UNIT_MEASUREMENT` VALUES (1,'BAGS',1),(2,'Pack',1),(3,'Liter',1),(4,'Piece',1),
(5,'Bundle',1),(6,'Gallon',1),(7,'ml',1),(8,'Quart',1),(9,'KILO',1),(10,'Pail',1),
(11,'GRAMS',1),(12,'25 KG/BAG',1),(13,'50 KG/BAG',1);

-- RECEIPT_METHOD
SYSTEM echo 'Inserting RECEIPT_METHOD';
INSERT INTO `RECEIPT_METHOD` VALUES (1,'CASH ON HAND',1,2,2,NULL,1,1,NOW(),1,NOW());

-- INVENTORY_ACCOUNT
SYSTEM echo 'Inserting INVENTORY_ACCOUNT';
INSERT INTO `INVENTORY_ACCOUNT` VALUES (1,1,1,1,1,1,NOW(),1,NOW());

SYSTEM echo 'Inserting STOCK_ADJUSTMENT_CLASSIFICATION';
source ~/CMS/src/eulap/eb/conf/db/data/insert_stock_adjusment_out_stock_adjustment_classification_02_20_2018_16_33.sql

SYSTEM echo "inserting ar receipt object type";
source ~/CMS/src/eulap/eb/conf/db/update/update_ar_receipt_add_eb_object_id_12_12_2017_15_47.sql
