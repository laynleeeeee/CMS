
-- Description: Consolidated insert scripts for CMS accounting default data.

-- GL_ENTRY_SOURCE
SYSTEM echo 'Inserting GL_ENTRY_SOURCE';
INSERT INTO `GL_ENTRY_SOURCE` VALUES (1,'General Ledger');

-- ACCOUNTING OBJECT TYPES
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_accounting_object_types.sql

-- ACCOUNTING OR TYPES
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_accounting_or_types.sql

-- GL_STATUS
source ~/CMS/src/eulap/eb/conf/db/data/insert_gl_status.sql

-- BUS_REG_TYPE
source ~/CMS/src/eulap/eb/conf/db/data/insert_business_registration_type.sql

-- NORMAL_BALANCE
source ~/CMS/src/eulap/eb/conf/db/data/insert_normal_balance.sql

-- TIME_PERIOD_STATUS
source ~/CMS/src/eulap/eb/conf/db/data/insert_time_period_status.sql

SYSTEM echo "insert_invoice_type_10_07_2015_14_14.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_invoice_type_10_07_2015_14_14.sql

SYSTEM echo "insert_ar_transaction_type_10_07_2015_14_15.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_ar_transaction_type_10_07_2015_14_15.sql

SYSTEM echo "insert_ar_miscellaneous_type_10_28_2015_16_37.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_ar_miscellaneous_type_10_28_2015_16_37.sql

SYSTEM echo "insert_ar_receipt_type_10_07_2015_14_15.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_ar_receipt_type_10_07_2015_14_15.sql

SYSTEM echo "insert_account_type_10_07_2015_14_33.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_account_type_10_07_2015_14_33.sql

SYSTEM echo "insert_account_10_07_2015_14_34.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_account_10_07_2015_14_34.sql

-- Enable only for NSB project
source ~/CMS/src/eulap/eb/conf/db/nsb/data/insert_nsb_default_division_06_11_2021_10_27.sql

SYSTEM echo "insert_account_combination_10_07_2015_14_35.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_account_combination_10_07_2015_14_35.sql

SYSTEM echo "insert_checkbook_template_12_09_2015_13_49.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_checkbook_template_12_09_2015_13_49.sql

SYSTEM echo "consolidated_insert_withholding_tax_data.sql";
source ~/CMS/src/eulap/eb/conf/db/data/consolidated_insert_withholding_tax_data.sql

SYSTEM echo "insert_default_vat_account_07_01_2020_09_13.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_default_vat_account_07_01_2020_09_13.sql

-- RECEIPT_METHOD
SYSTEM echo 'Inserting RECEIPT_METHOD';
INSERT INTO `RECEIPT_METHOD` VALUES (1,'CASH ON HAND',1,35,32,NULL,1,1,NOW(),1,NOW());

SYSTEM echo "inserting payment type default value";
source ~/CMS/src/eulap/eb/conf/db/data/insert_payment_type_default_value_06_01_2018_11_22_01.sql
