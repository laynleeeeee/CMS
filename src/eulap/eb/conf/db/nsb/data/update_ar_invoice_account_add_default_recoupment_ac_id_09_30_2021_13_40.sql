
-- Description	: Insert default recoupment accounts for ar invoice account.

UPDATE AR_INVOICE_ACCOUNT SET RECOUPMENT_AC_ID = 533 WHERE DIVISION_ID = 1;
UPDATE AR_INVOICE_ACCOUNT SET RECOUPMENT_AC_ID = 829 WHERE DIVISION_ID = 2;
UPDATE AR_INVOICE_ACCOUNT SET RECOUPMENT_AC_ID = 1340 WHERE DIVISION_ID = 3;
UPDATE AR_INVOICE_ACCOUNT SET RECOUPMENT_AC_ID = 1851 WHERE DIVISION_ID = 4;
UPDATE AR_INVOICE_ACCOUNT SET RECOUPMENT_AC_ID = 2362 WHERE DIVISION_ID = 5;
UPDATE AR_INVOICE_ACCOUNT SET RECOUPMENT_AC_ID = 2873 WHERE DIVISION_ID = 6;