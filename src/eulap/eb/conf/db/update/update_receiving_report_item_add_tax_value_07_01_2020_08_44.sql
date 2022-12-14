
-- Description	: Add TAX_TYPE_ID and VAT_AMOUNT in R_RECEIVING_REPORT_ITEM table.

ALTER TABLE R_RECEIVING_REPORT_ITEM ADD COLUMN TAX_TYPE_ID INT(10) UNSIGNED DEFAULT NULL,
ADD CONSTRAINT FK_RRI_TAX_TYPE_ID FOREIGN KEY (TAX_TYPE_ID) REFERENCES TAX_TYPE (TAX_TYPE_ID);

ALTER TABLE R_RECEIVING_REPORT_ITEM ADD COLUMN VAT_AMOUNT DOUBLE DEFAULT 0;