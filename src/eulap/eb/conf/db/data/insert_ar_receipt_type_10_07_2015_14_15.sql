
-- Description: Insert script AR_RECEIPT_TYPE for Elasticbooks clean db.

-- AR_RECEIPT_TYPE
SYSTEM echo 'Inserting AR_RECEIPT_TYPE';
INSERT INTO `AR_RECEIPT_TYPE` 
VALUES (1,'CASH',1,NOW(),1,NOW()),
(2,'CHECK',1,NOW(),1,NOW()),
(3,'BANK TRANSFER',1,NOW(),1,NOW());
