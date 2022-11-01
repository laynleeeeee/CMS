
-- Description	: Insert scripts to add EWT WC 230; 30% withholding tax account

INSERT INTO ACCOUNT (NUMBER, ACCOUNT_NAME, DESCRIPTION, ACCOUNT_TYPE_ID, PARENT_ACCOUNT_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES ('4001040006', 'EWT WC 230; 30%', 'EWT WC 230; 30%', (SELECT ACCOUNT_TYPE_ID FROM ACCOUNT_TYPE WHERE NAME='Current Liabilities'),
(SELECT A.ACCOUNT_ID FROM ACCOUNT A WHERE ACCOUNT_NAME='Creditable Withholding Tax'), 1, 1, NOW(), 1, NOW(), 1);

INSERT INTO ACCOUNT_COMBINATION (COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
SELECT 1, ACCOUNT_ID, 1, 1, 1, NOW(), 1, NOW(), 1 FROM ACCOUNT
WHERE ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE NUMBER = '4001040006');

INSERT INTO ACCOUNT_COMBINATION (COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
SELECT 1, ACCOUNT_ID, 2, 1, 1, NOW(), 1, NOW(), 1 FROM ACCOUNT
WHERE ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE NUMBER = '4001040006');

INSERT INTO ACCOUNT_COMBINATION (COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
SELECT 1, ACCOUNT_ID, 3, 1, 1, NOW(), 1, NOW(), 1 FROM ACCOUNT
WHERE ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE NUMBER = '4001040006');

INSERT INTO ACCOUNT_COMBINATION (COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
SELECT 1, ACCOUNT_ID, 4, 1, 1, NOW(), 1, NOW(), 1 FROM ACCOUNT
WHERE ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE NUMBER = '4001040006');

INSERT INTO ACCOUNT_COMBINATION (COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
SELECT 1, ACCOUNT_ID, 5, 1, 1, NOW(), 1, NOW(), 1 FROM ACCOUNT
WHERE ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE NUMBER = '4001040006');

INSERT INTO ACCOUNT_COMBINATION (COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
SELECT 1, ACCOUNT_ID, 6, 1, 1, NOW(), 1, NOW(), 1 FROM ACCOUNT
WHERE ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE NUMBER = '4001040006');

INSERT INTO WT_ACCOUNT_SETTING (COMPANY_ID, NAME, VALUE, ACCOUNT_COMBINATION_ID, ACTIVE, ORDER_ID, CREDITABLE)
VALUES (1, 'EWT WC 230; 30%', 30, (SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE DIVISION_ID = 1 
AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE NUMBER = '4001040006')), 1, 6, 0),
(1, 'EWT WC 230; 30%', 30, (SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE DIVISION_ID = 2 
AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE NUMBER = '4001040006')), 1, 6, 0),
(1, 'EWT WC 230; 30%', 30, (SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE DIVISION_ID = 3
AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE NUMBER = '4001040006')), 1, 6, 0),
(1, 'EWT WC 230; 30%', 30, (SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE DIVISION_ID = 4 
AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE NUMBER = '4001040006')), 1, 6, 0),
(1, 'EWT WC 230; 30%', 30, (SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE DIVISION_ID = 5 
AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE NUMBER = '4001040006')), 1, 6, 0),
(1, 'EWT WC 230; 30%', 30, (SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE DIVISION_ID = 6 
AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE NUMBER = '4001040006')), 1, 6, 0);