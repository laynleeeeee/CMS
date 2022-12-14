
-- Description : Consolidated update script that will add TAX_TYPE_ID to VAT_ACCOUNT_SETUP and insert its respective data

INSERT INTO VAT_ACCOUNT_SETUP (COMPANY_ID, DIVISION_ID, TAX_TYPE_ID, INPUT_VAT_AC_ID, OUTPUT_VAT_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 1, 10, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 1 AND AC.ACCOUNT_ID = 38), NULL, 1, 1, NOW(), 1, NOW()),
(1, 2, 10, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 2 AND AC.ACCOUNT_ID = 38), NULL, 1, 1, NOW(), 1, NOW()),
(1, 3, 10, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 3 AND AC.ACCOUNT_ID = 38), NULL, 1, 1, NOW(), 1, NOW()),
(1, 4, 10, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 4 AND AC.ACCOUNT_ID = 38), NULL, 1, 1, NOW(), 1, NOW()),
(1, 5, 10, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 5 AND AC.ACCOUNT_ID = 38), NULL, 1, 1, NOW(), 1, NOW()),
(1, 6, 10, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 6 AND AC.ACCOUNT_ID = 38), NULL, 1, 1, NOW(), 1, NOW());