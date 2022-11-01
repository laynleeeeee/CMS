
-- Description: Add REF_CASH_SALE_RETURN_ID in CASH_SALE_RETURN tables.

ALTER TABLE CASH_SALE_RETURN ADD REF_CASH_SALE_RETURN_ID int(10) unsigned DEFAULT NULL AFTER CASH_SALE_ID,
ADD CONSTRAINT FK_CSR_REF_CASH_SALE_RETURN_ID FOREIGN KEY (REF_CASH_SALE_RETURN_ID)
REFERENCES CASH_SALE_RETURN (CASH_SALE_RETURN_ID);