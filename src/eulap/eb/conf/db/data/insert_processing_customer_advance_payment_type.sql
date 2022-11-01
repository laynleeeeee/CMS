
-- Description: Insert script for customer advance payment types of processing modules.

-- CUSTOMER_ADVANCE_PAYMENT_TYPE
SYSTEM echo 'Inserting CUSTOMER_ADVANCE_PAYMENT_TYPE';
INSERT INTO CUSTOMER_ADVANCE_PAYMENT_TYPE VALUES (3, 'Individual Selection', 1, 1 , NOW(), 1, NOW());
INSERT INTO CUSTOMER_ADVANCE_PAYMENT_TYPE VALUES (5, 'WIPSO', 1, 1 , NOW(), 1, NOW());