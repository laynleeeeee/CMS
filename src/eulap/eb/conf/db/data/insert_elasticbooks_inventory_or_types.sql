

-- Description: Insert script inventory module OR Types.

SYSTEM echo 'Inserting inventory OR_TYPE';
INSERT INTO OR_TYPE VALUES (3, 'Stock-Adjustment-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (4, 'Cash-Sales-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (5, 'Account-Sales-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (6, 'Cash-Sales-Return-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (7, 'Account-Sales-Return-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (8, 'Transfer-Receipt-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (10, 'Return-To-Supplier-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (11, 'Customer-Advance-Payment-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (12, 'Paid-in-Advance-Delivery-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (13, 'Receiving-Report-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (14, 'Purchase-Order-Relationship', 1, 1, now(), 1, now());
