
-- Description: Modify BANK_ACCOUNT_ID, CHECKBOOK_ID, CHECK_DATE column definition.

ALTER TABLE AP_PAYMENT MODIFY BANK_ACCOUNT_ID INT(10) unsigned DEFAULT NULL;
ALTER TABLE AP_PAYMENT MODIFY CHECKBOOK_ID INT(10) unsigned DEFAULT NULL;
ALTER TABLE AP_PAYMENT MODIFY CHECK_DATE DATE DEFAULT NULL;