
-- Description: Insert script for sales order types.

-- SO_TYPE
SYSTEM echo 'Inserting SO_TYPE'; 
INSERT INTO SO_TYPE VALUES (1,'PO',1,1,NOW(),1,NOW());
INSERT INTO SO_TYPE VALUES (2,'PCR',1,1,NOW(),1,NOW());
