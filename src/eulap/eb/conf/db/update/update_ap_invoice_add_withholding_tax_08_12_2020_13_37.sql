
-- Description	: Add WT_ACCOUNT_SETTING_ID and WT_AMOUNT in AP_INVOICE table

ALTER TABLE AP_INVOICE ADD COLUMN WT_ACCOUNT_SETTING_ID INT(10) UNSIGNED DEFAULT NULL,
ADD CONSTRAINT FK_AP_INVOICE_WT_ACCOUNT_SETTING_ID FOREIGN KEY (WT_ACCOUNT_SETTING_ID)
REFERENCES WT_ACCOUNT_SETTING (WT_ACCOUNT_SETTING_ID);

ALTER TABLE AP_INVOICE ADD COLUMN WT_AMOUNT DOUBLE DEFAULT 0;