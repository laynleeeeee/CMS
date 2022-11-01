
-- Description: Insert script for inventory ar transaction types.

-- AR_TRANSACTION_TYPE
SYSTEM echo 'Inserting AR_TRANSACTION_TYPE';
INSERT INTO `AR_TRANSACTION_TYPE` VALUES (4,1,'Account Sale',1,1,NOW(),1,NOW());
INSERT INTO `AR_TRANSACTION_TYPE` VALUES (5,1,'Sales Return',1,1,NOW(),1,NOW());
