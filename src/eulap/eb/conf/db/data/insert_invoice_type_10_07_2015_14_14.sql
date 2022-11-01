
-- Description: Insert script INVOICE_TYPE for eb-fa clean db.

-- INVOICE_TYPE
SYSTEM echo 'Inserting INVOICE_TYPE'; 
INSERT INTO `INVOICE_TYPE` VALUES 
(1,1,'Regular Invoice',1,1,NOW(),1,NOW()),
(2,1,'Prepaid Invoice',1,1,NOW(),1,NOW()),
(3,1,'Debit Memo',1,1,NOW(),1,NOW()),
(4,1,'Credit Memo',1,1,NOW(),1,NOW());
