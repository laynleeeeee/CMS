
-- Description: Insert script for processing cash sale types.

-- CASH_SALE_TYPE
SYSTEM echo 'Inserting CASH_SALE_TYPE';
INSERT INTO CASH_SALE_TYPE VALUES (3, 'Individual Selection', 1, 1 , NOW(), 1, NOW());
INSERT INTO CASH_SALE_TYPE VALUES (6, 'Cash Sales - Processing', 1, 1 , NOW(), 1, NOW());
