
-- Description : Consolidated update script that will add TAX_TYPE_ID to VAT_ACCOUNT_SETUP and insert its respective data

DELETE FROM VAT_ACCOUNT_SETUP;

ALTER TABLE VAT_ACCOUNT_SETUP ADD COLUMN TAX_TYPE_ID INT (10) UNSIGNED DEFAULT NULL AFTER DIVISION_ID;

ALTER TABLE VAT_ACCOUNT_SETUP MODIFY COLUMN INPUT_VAT_AC_ID INT (10) UNSIGNED DEFAULT NULL;

ALTER TABLE VAT_ACCOUNT_SETUP MODIFY COLUMN OUTPUT_VAT_AC_ID INT (10) UNSIGNED DEFAULT NULL;

INSERT INTO VAT_ACCOUNT_SETUP (COMPANY_ID, DIVISION_ID, TAX_TYPE_ID, INPUT_VAT_AC_ID, OUTPUT_VAT_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 1, 4, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 1 AND AC.ACCOUNT_ID = 37), NULL, 1, 1, NOW(), 1, NOW()),
(1, 1, 5, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 1 AND AC.ACCOUNT_ID = 36), NULL, 1, 1, NOW(), 1, NOW()),
(1, 1, 6, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 1 AND AC.ACCOUNT_ID = 40), NULL, 1, 1, NOW(), 1, NOW()),
(1, 1, 7, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 1 AND AC.ACCOUNT_ID = 39), NULL, 1, 1, NOW(), 1, NOW()),
(1, 1, 8, NULL, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 1 AND AC.ACCOUNT_ID = 89), 1, 1, NOW(), 1, NOW()),
(1, 1, 9, NULL, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 1 AND AC.ACCOUNT_ID = 90), 1, 1, NOW(), 1, NOW()),
(1, 2, 4, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 2 AND AC.ACCOUNT_ID = 37), NULL, 1, 1, NOW(), 1, NOW()),
(1, 2, 5, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 2 AND AC.ACCOUNT_ID = 36), NULL, 1, 1, NOW(), 1, NOW()),
(1, 2, 6, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 2 AND AC.ACCOUNT_ID = 40), NULL, 1, 1, NOW(), 1, NOW()),
(1, 2, 7, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 2 AND AC.ACCOUNT_ID = 39), NULL, 1, 1, NOW(), 1, NOW()),
(1, 2, 8, NULL, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 2 AND AC.ACCOUNT_ID = 89), 1, 1, NOW(), 1, NOW()),
(1, 2, 9, NULL, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 2 AND AC.ACCOUNT_ID = 90), 1, 1, NOW(), 1, NOW()),
(1, 3, 4, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 3 AND AC.ACCOUNT_ID = 37), NULL, 1, 1, NOW(), 1, NOW()),
(1, 3, 5, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 3 AND AC.ACCOUNT_ID = 36), NULL, 1, 1, NOW(), 1, NOW()),
(1, 3, 6, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 3 AND AC.ACCOUNT_ID = 40), NULL, 1, 1, NOW(), 1, NOW()),
(1, 3, 7, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 3 AND AC.ACCOUNT_ID = 39), NULL, 1, 1, NOW(), 1, NOW()),
(1, 3, 8, NULL, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 3 AND AC.ACCOUNT_ID = 89), 1, 1, NOW(), 1, NOW()),
(1, 3, 9, NULL, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 3 AND AC.ACCOUNT_ID = 90), 1, 1, NOW(), 1, NOW()),
(1, 4, 4, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 4 AND AC.ACCOUNT_ID = 37), NULL, 1, 1, NOW(), 1, NOW()),
(1, 4, 5, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 4 AND AC.ACCOUNT_ID = 36), NULL, 1, 1, NOW(), 1, NOW()),
(1, 4, 6, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 4 AND AC.ACCOUNT_ID = 40), NULL, 1, 1, NOW(), 1, NOW()),
(1, 4, 7, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 4 AND AC.ACCOUNT_ID = 39), NULL, 1, 1, NOW(), 1, NOW()),
(1, 4, 8, NULL, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 4 AND AC.ACCOUNT_ID = 89), 1, 1, NOW(), 1, NOW()),
(1, 4, 9, NULL, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 4 AND AC.ACCOUNT_ID = 90), 1, 1, NOW(), 1, NOW()),
(1, 5, 4, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 5 AND AC.ACCOUNT_ID = 37), NULL, 1, 1, NOW(), 1, NOW()),
(1, 5, 5, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 5 AND AC.ACCOUNT_ID = 36), NULL, 1, 1, NOW(), 1, NOW()),
(1, 5, 6, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 5 AND AC.ACCOUNT_ID = 40), NULL, 1, 1, NOW(), 1, NOW()),
(1, 5, 7, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 5 AND AC.ACCOUNT_ID = 39), NULL, 1, 1, NOW(), 1, NOW()),
(1, 5, 8, NULL, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 5 AND AC.ACCOUNT_ID = 89), 1, 1, NOW(), 1, NOW()),
(1, 5, 9, NULL, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 5 AND AC.ACCOUNT_ID = 90), 1, 1, NOW(), 1, NOW()),
(1, 6, 4, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 6 AND AC.ACCOUNT_ID = 37), NULL, 1, 1, NOW(), 1, NOW()),
(1, 6, 5, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 6 AND AC.ACCOUNT_ID = 36), NULL, 1, 1, NOW(), 1, NOW()),
(1, 6, 6, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 6 AND AC.ACCOUNT_ID = 40), NULL, 1, 1, NOW(), 1, NOW()),
(1, 6, 7, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 6 AND AC.ACCOUNT_ID = 39), NULL, 1, 1, NOW(), 1, NOW()),
(1, 6, 8, NULL, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 6 AND AC.ACCOUNT_ID = 89), 1, 1, NOW(), 1, NOW()),
(1, 6, 9, NULL, (SELECT AC.ACCOUNT_COMBINATION_ID FROM ACCOUNT_COMBINATION AC WHERE AC.DIVISION_ID = 6 AND AC.ACCOUNT_ID = 90), 1, 1, NOW(), 1, NOW());