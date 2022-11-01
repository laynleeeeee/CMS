
-- Description: Insert script AR_TRANSACTION_TYPE for eb-fa clean db.

-- AR_TRANSACTION_TYPE
SYSTEM echo 'Inserting AR_TRANSACTION_TYPE';
INSERT INTO `AR_TRANSACTION_TYPE` VALUES
(1,1,'Regular Transaction',1,1,NOW(),1,NOW()),
(2,1,'Debit Memo',1,1,NOW(),1,NOW()),
(3,1,'Credit Memo',1,1,NOW(),1,NOW());