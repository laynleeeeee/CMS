
-- Description: Add EB_OJBECT_ID in CASH_SALE_RETURN and CASH_SALE_RETURN_ITEM tables.

ALTER TABLE CASH_SALE_RETURN_ITEM ADD EB_OBJECT_ID int(10) unsigned DEFAULT NULL AFTER CASH_SALE_ITEM_ID,
ADD CONSTRAINT FK_CSRI_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID);

ALTER TABLE CASH_SALE_RETURN ADD EB_OBJECT_ID int(10) unsigned DEFAULT NULL AFTER CASH_SALE_ID,
ADD CONSTRAINT FK_CSR_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID);
