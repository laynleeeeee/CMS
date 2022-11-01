-- Data migration script from customerAccount to CBS schema.


DELETE FROM CBS.ACCOUNT_RECEIVABLE;
DELETE FROM CBS.CUSTOMER_ACCT_SETTING;
DELETE FROM CBS.CUSTOMER;


INSERT INTO CBS.CUSTOMER (CUSTOMER_ID, COMPANY_ID, NAME, CONTACT_NUMBER, ADDRESS, EMAIL_ADDRESS, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE) SELECT CUSTOMER_ID, 1 as COMPANY_ID, CONCAT(First_Name ,' ', Last_Name) as NAME, Contact_Number, Address, 'empy@email.com', 1, 1, NOW(), 1, NOW() FROM useraccounts.customerprofile;

INSERT INTO CBS.CUSTOMER_ACCT_SETTING (CUSTOMER_ID, INTEREST_RATE, MAX_AMOUNT, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE) SELECT Customer_ID, Interest, 10000, 1, NOW(), 1, NOW() FROM useraccounts.customerprofile;

INSERT INTO CBS.ACCOUNT_RECEIVABLE (ACCOUNT_RECEIVABLE_ID, DATE, REFERENCE_ID, DESCRIPTION, PRINCIPAL, AMOUNT, DUE_DATE, DELETED, CUSTOMER_ID, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE )
SELECT Accnt_Rec_ID, borrowed_Date, Reference_Number, Particular, Amount, Amount, borrowed_date as DUE_DATE, 0, Customer_ID, 1, NOW(), 1, NOW() FROM useraccounts.accountsreceivable;

