
-- Description: Consolidated insert scripts for CMS processing default data.

SYSTEM echo "INSERT PROCESSING OBJECT TYPES";
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_processing_object_types.sql

SYSTEM echo "INSERT PROCESSING OR TYPES";
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_processing_or_types.sql

SYSTEM echo "insert_processing_stock_adjusment_classifications";
source ~/CMS/src/eulap/eb/conf/db/data/insert_processing_stock_adjusment_classifications.sql

SYSTEM echo "insert_processing_invoice_type";
source ~/CMS/src/eulap/eb/conf/db/data/insert_processing_invoice_type.sql

SYSTEM echo "insert_processing_ar_transaction_type";
source ~/CMS/src/eulap/eb/conf/db/data/insert_processing_ar_transaction_type.sql

SYSTEM echo "insert_processing_cash_sale_type";
source ~/CMS/src/eulap/eb/conf/db/data/insert_processing_cash_sale_type.sql

SYSTEM echo "insert_processing_transfer_receipt_type";
INSERT INTO TRANSFER_RECEIPT_TYPE VALUES (2, 'Individual Selection', 1, 1, now(), 1, now());

SYSTEM echo "insert_processing_customer_advance_payment_type";
source ~/CMS/src/eulap/eb/conf/db/data/insert_processing_customer_advance_payment_type.sql

SYSTEM echo "insert_processing_report_type_08_19_2015_15_45.sql";
source ~/CMS/src/eulap/eb/conf/db/data/insert_processing_report_type_08_19_2015_15_45.sql

-- Enable if creating database for IS forms
-- INSERT INTO UOM_CONVERSION (UOM_FROM, UOM_TO, NAME, VALUE, ACTIVE)
-- VALUES (12, 9, '25KG TO KG', 24.75, 1),
-- (12, 12, '25KG TO 25KG', 1, 1),
-- (12, 13, '25KG TO 50KG', 0.5, 1),
-- (13, 9, '50KG TO KG', 49.5, 1),
-- (13, 12, '50KG TO 25KG', 2, 1),
-- (13, 13, '50KG TO 50KG', 1, 1);
