
-- Description	: Inactivate duplicate advances from customer account.

UPDATE ACCOUNT SET ACTIVE = 0 WHERE ACCOUNT_ID = 57;