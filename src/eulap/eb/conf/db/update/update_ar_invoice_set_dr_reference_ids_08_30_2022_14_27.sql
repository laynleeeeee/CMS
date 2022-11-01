
-- Description	: Update script that will set DR reference id column in AR invoice

UPDATE AR_INVOICE SET DR_REFERENCE_IDS = DELIVERY_RECEIPT_ID WHERE DELIVERY_RECEIPT_ID IS NOT NULL;