
-- Description	: Add TAX_TYPE_ID in SERIAL_ITEM table

ALTER TABLE SERIAL_ITEM ADD COLUMN TAX_TYPE_ID INT(10) UNSIGNED DEFAULT NULL,
ADD CONSTRAINT FK_SI_TAX_TYPE_ID FOREIGN KEY (TAX_TYPE_ID) REFERENCES TAX_TYPE (TAX_TYPE_ID);

ALTER TABLE SERIAL_ITEM ADD COLUMN VAT_AMOUNT DOUBLE DEFAULT 0;

ALTER TABLE SERIAL_ITEM ADD COLUMN PO_NUMBER varchar(20) DEFAULT NULL;