
-- Description	: Consolidated insert script to add AR invoice temporary account

INSERT INTO EB_OBJECT (OBJECT_TYPE_ID, CREATED_BY, CREATED_DATE) VALUES (100, 1, NOW());

INSERT INTO ACCOUNT (NUMBER, ACCOUNT_NAME, DESCRIPTION, ACCOUNT_TYPE_ID, RELATED_ACCOUNT_ID, EB_OBJECT_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES ('1111', 'ADVANCES FROM CUSTOMER', 'ADVANCES FROM CUSTOMER', 9, NULL, (SELECT EB_OBJECT_ID FROM EB_OBJECT ORDER BY EB_OBJECT_ID DESC LIMIT 1), 1, 1, NOW(), 1, NOW(), 1);

INSERT INTO ACCOUNT_COMBINATION (COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (1, (SELECT ACCOUNT_ID FROM ACCOUNT WHERE EB_OBJECT_ID = (SELECT EB_OBJECT_ID FROM EB_OBJECT ORDER BY EB_OBJECT_ID DESC LIMIT 1)), 1, 1, 1, NOW(), 1, NOW(), 1);

INSERT INTO CUSTOMER_ADVANCE_PAYMENT_ACCOUNT (COMPANY_ID, DIVISION_ID, DEFAULT_CREDIT_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 1, (SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE DIVISION_ID = 1 AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE EB_OBJECT_ID = (SELECT EB_OBJECT_ID FROM EB_OBJECT ORDER BY EB_OBJECT_ID DESC LIMIT 1))),
1, 1, NOW(), 1, NOW());