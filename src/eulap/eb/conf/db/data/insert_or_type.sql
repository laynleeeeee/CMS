

-- Description: Insert scripts to insert OR Types

SYSTEM echo 'Inserting OR_TYPE';
INSERT INTO OR_TYPE VALUES (1, 'Parent-to-Child-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (2, 'Raw-to-Processed-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (3, 'Stock-Adjustment-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (4, 'Cash-Sales-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (5, 'Account-Sales-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (6, 'Cash-Sales-Return-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (7, 'Account-Sales-Return-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (8, 'Transfer-Receipt-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (9, 'Reference-Document-Relationship', 1, 1, now(), 1, now());

-- OR_TYPE_ID 11 = Customer-Advance-Payment-Relationship
source ~/CMS/src/eulap/eb/conf/db/data/insert_cap_or_type_06_28_2016_11_39.sql

-- OR_TYPE_ID 12 = Paid-in-Advance-Delivery-Relationship
source ~/CMS/src/eulap/eb/conf/db/data/insert_cap_delivery_or_type_06_28_2016_11_48.sql

-- OR_TYPE_ID 13 = Receiving-Report-Relationship
-- OR_TYPE_ID 14 = Purchase-Order-Relationship
source ~/CMS/src/eulap/eb/conf/db/data/insert_rr_po_or_type_08_23_2016_10_25.sql

-- OR_TYPE_ID 15 = Item-Conversion-Relationship
source ~/CMS/src/eulap/eb/conf/db/data/insert_item_conversion_or_type_09_16_2016_12_00.sql

-- OR_TYPE_ID 16 = Cash-Sale-Processed-Relationship
source ~/CMS/src/eulap/eb/conf/db/data/insert_cs_processing_or_type_09_26_2016_10_29.sql

-- OR_TYPE_ID 17 = WIP-Special-Order-Relationship
source ~/CMS/src/eulap/eb/conf/db/data/insert_wip_special_order_or_type_09_27_2016_13_14.sql

INSERT INTO OR_TYPE VALUES (99, 'Direct-Payment-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (100, 'RR-Raw-Materials-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (101, 'General-Ledger-AR-Line-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (102, 'Account-Sales-Po-Ar-Transaction-Relationship', 1, 1, now(), 1, now());
