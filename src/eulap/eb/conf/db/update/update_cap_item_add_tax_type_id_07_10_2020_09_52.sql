
-- Description	: Add TAX_TYPE_ID in CUSTOMER_ADVANCE_PAYMENT_ITEM table

ALTER TABLE CUSTOMER_ADVANCE_PAYMENT_ITEM ADD COLUMN TAX_TYPE_ID INT(10) UNSIGNED DEFAULT NULL,
ADD CONSTRAINT FK_CAPI_TAX_TYPE_ID FOREIGN KEY (TAX_TYPE_ID) REFERENCES TAX_TYPE (TAX_TYPE_ID);

ALTER TABLE CUSTOMER_ADVANCE_PAYMENT_ITEM ADD COLUMN VAT_AMOUNT DOUBLE DEFAULT 0;