
-- Description : Script to add receiving details columns on DIVISION_ID table.

ALTER TABLE DELIVERY_RECEIPT 
ADD DATE_RECEIVED DATE DEFAULT NULL,
ADD RECEIVER varchar (50) DEFAULT NULL;