
-- Description : Script to add columns on AR_MISCELLANEOUS table.

ALTER TABLE AR_MISCELLANEOUS ADD COMPANY_ID INT(10) UNSIGNED DEFAULT NULL AFTER EB_OBJECT_ID,
ADD CONSTRAINT FK_AR_MISCELLANEOUS_COMPANY_ID FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY (COMPANY_ID);

ALTER TABLE AR_MISCELLANEOUS ADD DIVISION_ID INT(10) UNSIGNED DEFAULT NULL AFTER AMOUNT,
ADD CONSTRAINT FK_AR_MISCELLANEOUS_DIVISION_ID FOREIGN KEY (DIVISION_ID) REFERENCES DIVISION (DIVISION_ID);

ALTER TABLE AR_MISCELLANEOUS ADD CURRENCY_ID INT(10) UNSIGNED DEFAULT NULL AFTER AMOUNT,
ADD CONSTRAINT FK_AR_MISCELLANEOUS_CURRENCY_ID FOREIGN KEY (CURRENCY_ID) REFERENCES CURRENCY (CURRENCY_ID);

ALTER TABLE AR_MISCELLANEOUS ADD CURRENCY_RATE_VALUE double DEFAULT 0 AFTER CURRENCY_ID;

ALTER TABLE AR_MISCELLANEOUS ADD CURRENCY_RATE_ID INT(10) UNSIGNED DEFAULT NULL AFTER CURRENCY_ID,
ADD CONSTRAINT FK_AR_MISCELLANEOUS_CURRENCY_RATE_ID FOREIGN KEY (CURRENCY_RATE_ID) REFERENCES CURRENCY_RATE (CURRENCY_RATE_ID);

ALTER TABLE AR_MISCELLANEOUS ADD COLUMN WT_VAT_AMOUNT double DEFAULT 0 AFTER WT_AMOUNT;