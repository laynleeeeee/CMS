
-- Description	: Add division id to warehouse table.

ALTER TABLE WAREHOUSE ADD DIVISION_ID int(10) unsigned DEFAULT NULL,
ADD CONSTRAINT FK_WAREHOUSE_DIVISION_ID FOREIGN KEY (DIVISION_ID) REFERENCES DIVISION (DIVISION_ID);