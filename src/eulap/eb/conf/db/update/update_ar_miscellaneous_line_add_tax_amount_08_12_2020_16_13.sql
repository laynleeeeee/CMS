
-- Description	: Add TAX_TYPE_ID and VAT_AMOUNT in AR_MISCELLANEOUS_LINE table.

ALTER TABLE AR_MISCELLANEOUS_LINE ADD COLUMN TAX_TYPE_ID INT(10) UNSIGNED DEFAULT NULL,
ADD CONSTRAINT FK_AR_MISCELLANEOUS_LINE_TAX_TYPE_ID FOREIGN KEY (TAX_TYPE_ID) REFERENCES TAX_TYPE (TAX_TYPE_ID);

ALTER TABLE AR_MISCELLANEOUS_LINE ADD COLUMN VAT_AMOUNT DOUBLE DEFAULT 0;