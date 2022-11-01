

-- Description: Insert script processing module OR Types.

SYSTEM echo 'Inserting processing OR_TYPE';
INSERT INTO OR_TYPE VALUES (2, 'Raw-to-Processed-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (15, 'Item-Conversion-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (16, 'CS-Processing-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (17, 'WIP-Special-Order-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (84, 'SAI-Bag-Qty-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (85, 'CSR-IS-Bag-Qty-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (86, 'CS-IS-Bag-Qty-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (87, 'AS-IS-Bag-Qty-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (88, 'ASR-IS-Bag-Qty-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (89, 'TR-IS-Bag-Qty-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (90, 'CAP-IS-Bag-Qty-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (91, 'PIAD-IS-Bag-Qty-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (92, 'SAO-Bag-Qty-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (95, 'PR-MAIN-RAW-MAT-Bag-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (96, 'PR-OTHER-MAT-Bag-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (97, 'PR-MAIN-PRODUCT-Bag-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (98, 'PR-BY-PRODUCT-Bag-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (100, 'RR-Raw-Materials-Relationship', 1, 1, now(), 1, now());
