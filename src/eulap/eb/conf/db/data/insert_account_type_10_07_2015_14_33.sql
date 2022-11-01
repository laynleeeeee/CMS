
-- Description: Insert script ACCOUNT_TYPE for eb-fa clean db.

-- ACCOUNT_TYPE
SYSTEM echo 'Inserting ACCOUNT_TYPE';
INSERT INTO `ACCOUNT_TYPE` VALUES 
(1,'Current Assets',1,1,0,1,1,NOW(),1,NOW()),
(2,'Non-Current Assets',1,1,0,1,1,NOW(),1,NOW()),
(3,'Current Liabilities',2,1,0,1,1,NOW(),1,NOW()),
(4,'Non-Current Liabilities',2,1,0,1,1,NOW(),1,NOW()),
(5,'Net Sales',2,1,0,1,1,NOW(),1,NOW()),
(6,'Cost of Sales',1,1,0,1,1,NOW(),1,NOW()),
(7,'Operating Expenses',1,1,0,1,1,NOW(),1,NOW()),
(8,'Equity',2,1,0,1,1,NOW(),1,NOW()),
(9,'Temporary Account',1,1,0,1,1,NOW(),1,NOW());