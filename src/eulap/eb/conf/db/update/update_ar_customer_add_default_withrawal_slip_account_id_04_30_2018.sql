
-- Description : Add DEFAULT_WITHDRAWAL_SLIP_AC_ID column in AR_CUSTOMER_ACCOUNT Table.

ALTER TABLE AR_CUSTOMER_ACCOUNT ADD COLUMN DEFAULT_WITHDRAWAL_SLIP_AC_ID INT(10) UNSIGNED DEFAULT NULL AFTER DEFAULT_AR_TRANSACTION_LINE_ID,
ADD CONSTRAINT FK_AR_CUST_ACCT_DEFAULT_WITHDRAWAL_SLIP_AC FOREIGN KEY (DEFAULT_WITHDRAWAL_SLIP_AC_ID) 
REFERENCES ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID);