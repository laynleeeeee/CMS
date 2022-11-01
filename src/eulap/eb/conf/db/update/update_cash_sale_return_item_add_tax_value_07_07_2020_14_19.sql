
-- Description	: Add TAX_TYPE_ID and VAT_AMOUNT in CASH_SALE_RETURN_ITEM table.

ALTER TABLE CASH_SALE_RETURN_ITEM ADD COLUMN TAX_TYPE_ID INT(10) UNSIGNED DEFAULT NULL,
ADD CONSTRAINT FK_CSRI_TAX_TYPE_ID FOREIGN KEY (TAX_TYPE_ID) REFERENCES TAX_TYPE (TAX_TYPE_ID);

ALTER TABLE CASH_SALE_RETURN_ITEM ADD COLUMN VAT_AMOUNT DOUBLE DEFAULT 0;