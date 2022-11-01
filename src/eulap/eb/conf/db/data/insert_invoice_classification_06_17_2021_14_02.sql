
-- Description: Insert script INVOICE_CLASSIFICATION for CMS clean db.

-- INVOICE_CLASSIFICATION
SYSTEM echo 'Inserting INVOICE_CLASSIFICATION'; 
INSERT INTO `INVOICE_CLASSIFICATION` VALUES 
(1,1,'Regular Invoice',1,1,NOW(),1,NOW()),
(2,1,'Prepaid Invoice',1,1,NOW(),1,NOW()),
(3,1,'Debit Memo',1,1,NOW(),1,NOW()),
(4,1,'Credit Memo',1,1,NOW(),1,NOW());
