
-- Description : Insert script to add default VAT_ACCOUNT_SETUP

INSERT INTO VAT_ACCOUNT_SETUP (COMPANY_ID, DIVISION_ID, INPUT_VAT_AC_ID, OUTPUT_VAT_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 1, (SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE COMPANY_ID = 1 AND DIVISION_ID = 1 AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE EB_OBJECT_ID = (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 100 LIMIT 38, 1))),
(SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE COMPANY_ID = 1 AND DIVISION_ID = 1 AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE EB_OBJECT_ID = (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 100 LIMIT 39, 1))),
1, 1, NOW(), 1, NOW());

-- Enable this for NSB project only
INSERT INTO VAT_ACCOUNT_SETUP (COMPANY_ID, DIVISION_ID, INPUT_VAT_AC_ID, OUTPUT_VAT_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 2, (SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE COMPANY_ID = 1 AND DIVISION_ID = 2 AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE EB_OBJECT_ID = (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 100 LIMIT 38, 1))),
(SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE COMPANY_ID = 1 AND DIVISION_ID = 2 AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE EB_OBJECT_ID = (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 100 LIMIT 39, 1))),
1, 1, NOW(), 1, NOW()),
(1, 3, (SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE COMPANY_ID = 1 AND DIVISION_ID = 3 AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE EB_OBJECT_ID = (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 100 LIMIT 38, 1))),
(SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE COMPANY_ID = 1 AND DIVISION_ID = 3 AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE EB_OBJECT_ID = (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 100 LIMIT 39, 1))),
1, 1, NOW(), 1, NOW()),
(1, 4, (SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE COMPANY_ID = 1 AND DIVISION_ID = 4 AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE EB_OBJECT_ID = (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 100 LIMIT 38, 1))),
(SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE COMPANY_ID = 1 AND DIVISION_ID = 4 AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE EB_OBJECT_ID = (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 100 LIMIT 39, 1))),
1, 1, NOW(), 1, NOW()),
(1, 5, (SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE COMPANY_ID = 1 AND DIVISION_ID = 5 AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE EB_OBJECT_ID = (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 100 LIMIT 38, 1))),
(SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE COMPANY_ID = 1 AND DIVISION_ID = 5 AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE EB_OBJECT_ID = (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 100 LIMIT 39, 1))),
1, 1, NOW(), 1, NOW()),
(1, 6, (SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE COMPANY_ID = 1 AND DIVISION_ID = 6 AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE EB_OBJECT_ID = (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 100 LIMIT 38, 1))),
(SELECT ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION WHERE COMPANY_ID = 1 AND DIVISION_ID = 6 AND ACCOUNT_ID = (SELECT ACCOUNT_ID FROM ACCOUNT WHERE EB_OBJECT_ID = (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 100 LIMIT 39, 1))),
1, 1, NOW(), 1, NOW());