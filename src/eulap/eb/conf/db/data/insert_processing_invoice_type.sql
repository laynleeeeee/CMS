
-- Description: Insert script for inventory invoice types.

-- INVOICE_TYPE
SYSTEM echo 'Inserting INVOICE_TYPE'; 
INSERT INTO `INVOICE_TYPE` VALUES (8,1,'Receiving Report - Raw Materials',1,1,NOW(),1,NOW());
INSERT INTO `INVOICE_TYPE` VALUES (9,1,'Receiving Report - RM Palay', 1,1,NOW(),1,NOW());
