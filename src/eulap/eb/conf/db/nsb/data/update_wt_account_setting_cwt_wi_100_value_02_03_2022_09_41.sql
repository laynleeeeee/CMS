
-- Description	: Update script that will update WT_ACCOUNT_SETTING AND ACCOUNT name and value for CWT WI 100

UPDATE WT_ACCOUNT_SETTING SET NAME = 'CWT WI100; 5%', VALUE = 5 WHERE NAME = 'CWT WI100; 1%';
UPDATE ACCOUNT SET ACCOUNT_NAME = 'CWT WI100; 5%', DESCRIPTION = 'CWT WI100; 5%' WHERE ACCOUNT_NAME = 'CWT WI100; 1%';