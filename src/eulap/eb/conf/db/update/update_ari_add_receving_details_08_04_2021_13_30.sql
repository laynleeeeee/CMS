
-- Description : Script to add receiving details columns on AR_INVOICE table.

ALTER TABLE AR_INVOICE 
ADD DATE_RECEIVED DATE DEFAULT NULL,
ADD RECEIVER varchar(50) DEFAULT NULL;