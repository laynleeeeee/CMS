
-- Description	: Update ap invoice add principal amount for invoice type ap loan.

ALTER TABLE AP_INVOICE ADD PRINCIPAL_LOAN double default 0; 
ALTER TABLE AP_INVOICE ADD PRINCIPAL_PAYMENT double default 0; 