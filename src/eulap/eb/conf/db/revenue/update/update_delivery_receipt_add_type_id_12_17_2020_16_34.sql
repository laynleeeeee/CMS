
-- Description	: Add DELIVERY_RECEIPT_TYPE_ID in DELIVERY_RECEIPT table

ALTER TABLE DELIVERY_RECEIPT ADD COLUMN DELIVERY_RECEIPT_TYPE_ID INT(10) UNSIGNED DEFAULT NULL,
ADD CONSTRAINT FK_DR_TYPE_ID FOREIGN KEY (DELIVERY_RECEIPT_TYPE_ID) REFERENCES DELIVERY_RECEIPT_TYPE (DELIVERY_RECEIPT_TYPE_ID);