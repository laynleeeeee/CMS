
-- Description: Insert script for processing ar transaction types.

-- AR_TRANSACTION_TYPE
SYSTEM echo 'Inserting AR_TRANSACTION_TYPE';
INSERT INTO `AR_TRANSACTION_TYPE` VALUES (10,1,'Account Sale - IS',1,1,NOW(),1,NOW());
INSERT INTO `AR_TRANSACTION_TYPE` VALUES (11,1,'Account Sale Return - IS',1,1,NOW(),1,NOW());
INSERT INTO `AR_TRANSACTION_TYPE` VALUES (15, 1, 'Sales Order', 1, 1, NOW(), 1, NOW());
