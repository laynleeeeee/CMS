
-- Description	: Consolidated NSB insert accounts and account combinations.

-- Supplier advance Payment
INSERT INTO EB_OBJECT (EB_OBJECT_ID, OBJECT_TYPE_ID, CREATED_BY, CREATED_DATE) VALUES (73, 100, 1, NOW());
-- ACCOUNT
INSERT INTO ACCOUNT (ACCOUNT_ID, NUMBER, ACCOUNT_NAME, DESCRIPTION, ACCOUNT_TYPE_ID, RELATED_ACCOUNT_ID, EB_OBJECT_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (60, '1011400000', 'Advances To Supplier', 'Advances To Supplier', 1, NULL, 73, 1, 1, NOW(), 1, NOW(), 1);
-- ACCOUNT COMBINATION
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (447, 1, 60, 1, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (448, 1, 60, 2, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (449, 1, 60, 3, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (450, 1, 60, 4, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (451, 1, 60, 5, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (452, 1, 60, 6, 1, 1, NOW(), 1, NOW(), 1);
-- SUPPLIER_ADV_PAYMENT_ACCOUNT
INSERT INTO SUPPLIER_ADV_PAYMENT_ACCOUNT (COMPANY_ID, DIVISION_ID, ACCOUNT_COMBINATION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 1, 447, 1, 1, NOW(), 1, NOW());
INSERT INTO SUPPLIER_ADV_PAYMENT_ACCOUNT (COMPANY_ID, DIVISION_ID, ACCOUNT_COMBINATION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 2, 448, 1, 1, NOW(), 1, NOW());
INSERT INTO SUPPLIER_ADV_PAYMENT_ACCOUNT (COMPANY_ID, DIVISION_ID, ACCOUNT_COMBINATION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 3, 449, 1, 1, NOW(), 1, NOW());
INSERT INTO SUPPLIER_ADV_PAYMENT_ACCOUNT (COMPANY_ID, DIVISION_ID, ACCOUNT_COMBINATION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 4, 450, 1, 1, NOW(), 1, NOW());
INSERT INTO SUPPLIER_ADV_PAYMENT_ACCOUNT (COMPANY_ID, DIVISION_ID, ACCOUNT_COMBINATION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 5, 451, 1, 1, NOW(), 1, NOW());
INSERT INTO SUPPLIER_ADV_PAYMENT_ACCOUNT (COMPANY_ID, DIVISION_ID, ACCOUNT_COMBINATION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 6, 452, 1, 1, NOW(), 1, NOW());

-- DELIVERY RECEIPT ACCOUNT
INSERT INTO DELIVERY_RECEIPT_ACCOUNT (COMPANY_ID, DIVISION_ID, DEFAULT_SERVICE_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 1, 14, 1, 1, NOW(), 1, NOW());
INSERT INTO DELIVERY_RECEIPT_ACCOUNT (COMPANY_ID, DIVISION_ID, DEFAULT_SERVICE_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 2, 57, 1, 1, NOW(), 1, NOW());
INSERT INTO DELIVERY_RECEIPT_ACCOUNT (COMPANY_ID, DIVISION_ID, DEFAULT_SERVICE_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 3, 120, 1, 1, NOW(), 1, NOW());
INSERT INTO DELIVERY_RECEIPT_ACCOUNT (COMPANY_ID, DIVISION_ID, DEFAULT_SERVICE_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 4, 183, 1, 1, NOW(), 1, NOW());
INSERT INTO DELIVERY_RECEIPT_ACCOUNT (COMPANY_ID, DIVISION_ID, DEFAULT_SERVICE_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 5, 246, 1, 1, NOW(), 1, NOW());
INSERT INTO DELIVERY_RECEIPT_ACCOUNT (COMPANY_ID, DIVISION_ID, DEFAULT_SERVICE_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 6, 309, 1, 1, NOW(), 1, NOW());

-- CUSTOMER ADVANCE PAYMENT
INSERT INTO EB_OBJECT (EB_OBJECT_ID, OBJECT_TYPE_ID, CREATED_BY, CREATED_DATE) VALUES (74, 100, 1, NOW());
-- ACCOUNT
INSERT INTO ACCOUNT (ACCOUNT_ID, NUMBER, ACCOUNT_NAME, DESCRIPTION, ACCOUNT_TYPE_ID, RELATED_ACCOUNT_ID, EB_OBJECT_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (61, '', 'ADVANCES FROM CUSTOMER', 'ADVANCES FROM CUSTOMER', 9, NULL, 74, 1, 1, NOW(), 1, NOW(), 1);
-- ACCOUNT COMBINATION
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (453, 1, 61, 1, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (454, 1, 61, 2, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (455, 1, 61, 3, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (456, 1, 61, 4, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (457, 1, 61, 5, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (458, 1, 61, 6, 1, 1, NOW(), 1, NOW(), 1);

INSERT INTO CUSTOMER_ADVANCE_PAYMENT_ACCOUNT (COMPANY_ID, DIVISION_ID, DEFAULT_CREDIT_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 1, 453, 1, 1, NOW(), 1, NOW());
INSERT INTO CUSTOMER_ADVANCE_PAYMENT_ACCOUNT (COMPANY_ID, DIVISION_ID, DEFAULT_CREDIT_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 2, 454, 1, 1, NOW(), 1, NOW());
INSERT INTO CUSTOMER_ADVANCE_PAYMENT_ACCOUNT (COMPANY_ID, DIVISION_ID, DEFAULT_CREDIT_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 3, 455, 1, 1, NOW(), 1, NOW());
INSERT INTO CUSTOMER_ADVANCE_PAYMENT_ACCOUNT (COMPANY_ID, DIVISION_ID, DEFAULT_CREDIT_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 4, 456, 1, 1, NOW(), 1, NOW());
INSERT INTO CUSTOMER_ADVANCE_PAYMENT_ACCOUNT (COMPANY_ID, DIVISION_ID, DEFAULT_CREDIT_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 5, 457, 1, 1, NOW(), 1, NOW());
INSERT INTO CUSTOMER_ADVANCE_PAYMENT_ACCOUNT (COMPANY_ID, DIVISION_ID, DEFAULT_CREDIT_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 6, 458, 1, 1, NOW(), 1, NOW());

-- AR INVOICE ACCOUNT
-- AR RETETENTION ACCOUNT COMBINATION
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (459, 1, 58, 2, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (460, 1, 58, 3, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (461, 1, 58, 4, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (462, 1, 58, 5, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (463, 1, 58, 6, 1, 1, NOW(), 1, NOW(), 1);
-- CREDITABLE WITHHOLDING TAX ACCOUT COMBINATION
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (464, 1, 59, 2, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (465, 1, 59, 3, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (466, 1, 59, 4, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (467, 1, 59, 5, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (468, 1, 59, 6, 1, 1, NOW(), 1, NOW(), 1);

INSERT INTO AR_INVOICE_ACCOUNT (COMPANY_ID, DIVISION_ID, RETENTION_AC_ID, WT_VAT_AC_ID, DEFAULT_DISC_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 2, 459, 464, 55, 1, 1, NOW(), 1, NOW());
INSERT INTO AR_INVOICE_ACCOUNT (COMPANY_ID, DIVISION_ID, RETENTION_AC_ID, WT_VAT_AC_ID, DEFAULT_DISC_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 3, 460, 465, 118, 1, 1, NOW(), 1, NOW());
INSERT INTO AR_INVOICE_ACCOUNT (COMPANY_ID, DIVISION_ID, RETENTION_AC_ID, WT_VAT_AC_ID, DEFAULT_DISC_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 4, 461, 466, 181, 1, 1, NOW(), 1, NOW());
INSERT INTO AR_INVOICE_ACCOUNT (COMPANY_ID, DIVISION_ID, RETENTION_AC_ID, WT_VAT_AC_ID, DEFAULT_DISC_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 5, 462, 467, 244, 1, 1, NOW(), 1, NOW());
INSERT INTO AR_INVOICE_ACCOUNT (COMPANY_ID, DIVISION_ID, RETENTION_AC_ID, WT_VAT_AC_ID, DEFAULT_DISC_AC_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (1, 6, 463, 468, 307, 1, 1, NOW(), 1, NOW());

-- VAT_ACCOUNT_SETUP
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (469, 1, 53, 2, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (470, 1, 53, 3, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (471, 1, 53, 4, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (472, 1, 53, 5, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (473, 1, 53, 6, 1, 1, NOW(), 1, NOW(), 1);

INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (474, 1, 54, 2, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (475, 1, 54, 3, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (476, 1, 54, 4, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (477, 1, 54, 5, 1, 1, NOW(), 1, NOW(), 1);
INSERT INTO ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID, COMPANY_ID, ACCOUNT_ID, DIVISION_ID, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES (478, 1, 54, 6, 1, 1, NOW(), 1, NOW(), 1);
-- UPDATE EXISTING ACCOUNTS
-- THE CURRENT ACCOUNT COMBINATIONS FOR VAT_ACCOUNT_SETUP INPUT AND OUTPUT VAT IS 0.
UPDATE VAT_ACCOUNT_SETUP SET INPUT_VAT_AC_ID = 469, OUTPUT_VAT_AC_ID = 474 WHERE DIVISION_ID = 2;
UPDATE VAT_ACCOUNT_SETUP SET INPUT_VAT_AC_ID = 470, OUTPUT_VAT_AC_ID = 475 WHERE DIVISION_ID = 3;
UPDATE VAT_ACCOUNT_SETUP SET INPUT_VAT_AC_ID = 471, OUTPUT_VAT_AC_ID = 476 WHERE DIVISION_ID = 4;
UPDATE VAT_ACCOUNT_SETUP SET INPUT_VAT_AC_ID = 472, OUTPUT_VAT_AC_ID = 477 WHERE DIVISION_ID = 5;
UPDATE VAT_ACCOUNT_SETUP SET INPUT_VAT_AC_ID = 473, OUTPUT_VAT_AC_ID = 478 WHERE DIVISION_ID = 6;
