
-- Description	: Add AR_INVOICE_TYPE_ID in AR_INVOICE table

ALTER TABLE AR_INVOICE ADD COLUMN AR_INVOICE_TYPE_ID INT(10) UNSIGNED DEFAULT NULL,
ADD CONSTRAINT FK_ARI_TYPE_ID FOREIGN KEY (AR_INVOICE_TYPE_ID) REFERENCES AR_INVOICE_TYPE (AR_INVOICE_TYPE_ID);