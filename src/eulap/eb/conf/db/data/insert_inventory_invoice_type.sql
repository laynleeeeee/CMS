
-- Description: Insert script for inventory invoice types.

-- INVOICE_TYPE
SYSTEM echo 'Inserting INVOICE_TYPE'; 
INSERT INTO `INVOICE_TYPE` VALUES (5,1,'Receiving Report',1,1,NOW(),1,NOW());
INSERT INTO `INVOICE_TYPE` VALUES (6,1,'Return To Supplier',1,1,NOW(),1,NOW());
