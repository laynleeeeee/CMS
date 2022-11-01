
-- Description : Script to add columns on AP_INVOICE table.

ALTER TABLE AP_INVOICE ADD CURRENCY_ID INT(10) UNSIGNED DEFAULT NULL AFTER DESCRIPTION,
ADD CONSTRAINT FK_AP_INVOICE_CURRENCY_ID FOREIGN KEY (CURRENCY_ID) REFERENCES CURRENCY (CURRENCY_ID);

ALTER TABLE AP_INVOICE ADD CURRENCY_RATE_VALUE double DEFAULT 0 AFTER CURRENCY_ID;

ALTER TABLE AP_INVOICE ADD CURRENCY_RATE_ID INT(10) UNSIGNED DEFAULT NULL AFTER CURRENCY_ID,
ADD CONSTRAINT FK_AP_INVOICE_CURRENCY_RATE_ID FOREIGN KEY (CURRENCY_RATE_ID) REFERENCES CURRENCY_RATE (CURRENCY_RATE_ID);