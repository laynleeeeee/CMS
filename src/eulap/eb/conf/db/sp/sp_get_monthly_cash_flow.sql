
-- Description	: SQL Stored Procedure for Cash Flow Report

Delimiter //
DROP PROCEDURE IF EXISTS GET_MONTHLY_CASH_FLOW;

CREATE PROCEDURE GET_MONTHLY_CASH_FLOW (IN IN_COMPANY_ID INT, IN IN_DIVISION_ID INT, IN IN_START_DATE DATE, IN IN_END_DATE DATE)

BEGIN

SELECT SOURCE, ACCOUNT_ID, ACCOUNT_NAME, ACCOUNT_TYPE_ID, LINE_AMOUNT, TR_AMOUNT,
PAID_AMOUNT, WITHHOLDING_TAX FROM (

-- AP Invoice - Goods/Services (Non-serialized item)
SELECT 'APP' AS SOURCE, CONCAT('API G/S ', AI.INVOICE_TYPE_ID, ' - ', AI.SEQUENCE_NO) AS REF_NO,
AC.ACCOUNT_ID, A.ACCOUNT_NAME, A.ACCOUNT_TYPE_ID, (IG.QUANTITY * COALESCE(IG.UNIT_COST, 0)) - COALESCE(IG.DISCOUNT, 0) AS LINE_AMOUNT,
AI.AMOUNT + COALESCE(AI.WT_AMOUNT, 0) AS TR_AMOUNT,
(SELECT SUM(APL.PAID_AMOUNT) FROM AP_PAYMENT_LINE APL
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID
INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32) 
AND OTO.OR_TYPE_ID = 24004 AND API.AP_INVOICE_ID = AI.AP_INVOICE_ID) AS PAID_AMOUNT,
COALESCE(AI.WT_AMOUNT, 0) AS WITHHOLDING_TAX
FROM AP_INVOICE_GOODS IG 
INNER JOIN AP_INVOICE AI ON AI.AP_INVOICE_ID = IG.AP_INVOICE_ID 
INNER JOIN AP_INVOICE_ACCOUNT AIA ON (AIA.COMPANY_ID = AI.COMPANY_ID AND AIA.DIVISION_ID = AI.DIVISION_ID) 
INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = AIA.ACCOUNT_COMBINATION_ID 
INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID 
INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.FROM_OBJECT_ID = AI.EB_OBJECT_ID
INNER JOIN AP_PAYMENT_LINE AL ON AL.EB_OBJECT_ID = O2O.TO_OBJECT_ID
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = AL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID 
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32)
AND AI.INVOICE_TYPE_ID IN (25, 26, 27, 28, 29, 30) 
AND O2O.OR_TYPE_ID = 24004
AND AI.COMPANY_ID = AP.COMPANY_ID
AND AP.COMPANY_ID = IN_COMPANY_ID
AND AP.DIVISION_ID = IN_DIVISION_ID
AND AP.CHECK_DATE BETWEEN IN_START_DATE AND IN_END_DATE
GROUP BY IG.AP_INVOICE_GOODS_ID

UNION ALL

-- AP Invoice - Goods/Services (Serialized item)
SELECT 'APP' AS SOURCE, CONCAT('API G/S ', AI.INVOICE_TYPE_ID, ' - ', AI.SEQUENCE_NO) AS REF_NO,
AC.ACCOUNT_ID, A.ACCOUNT_NAME, A.ACCOUNT_TYPE_ID, (IG.QUANTITY * COALESCE(IG.UNIT_COST, 0)) - COALESCE(IG.DISCOUNT, 0) AS LINE_AMOUNT,
AI.AMOUNT + COALESCE(AI.WT_AMOUNT, 0) AS TR_AMOUNT,
(SELECT SUM(APL.PAID_AMOUNT) FROM AP_PAYMENT_LINE APL
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID
INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32) 
AND OTO.OR_TYPE_ID = 24004 AND API.AP_INVOICE_ID = AI.AP_INVOICE_ID) AS PAID_AMOUNT,
COALESCE(AI.WT_AMOUNT, 0) AS WITHHOLDING_TAX
FROM SERIAL_ITEM IG 
INNER JOIN OBJECT_TO_OBJECT OTO1 ON OTO1.TO_OBJECT_ID = IG.EB_OBJECT_ID
INNER JOIN AP_INVOICE AI ON AI.EB_OBJECT_ID = OTO1.FROM_OBJECT_ID
INNER JOIN AP_INVOICE_ACCOUNT AIA ON (AIA.COMPANY_ID = AI.COMPANY_ID AND AIA.DIVISION_ID = AI.DIVISION_ID) 
INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = AIA.ACCOUNT_COMBINATION_ID 
INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID 
INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.FROM_OBJECT_ID = AI.EB_OBJECT_ID
INNER JOIN AP_PAYMENT_LINE AL ON AL.EB_OBJECT_ID = O2O.TO_OBJECT_ID
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = AL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID 
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32)
AND AI.INVOICE_TYPE_ID IN (25, 26, 27, 28, 29, 30) 
AND O2O.OR_TYPE_ID = 24004
AND AI.COMPANY_ID = AP.COMPANY_ID
AND AP.COMPANY_ID = IN_COMPANY_ID
AND AP.DIVISION_ID = IN_DIVISION_ID
AND AP.CHECK_DATE BETWEEN IN_START_DATE AND IN_END_DATE
GROUP BY IG.SERIAL_ITEM_ID 

UNION ALL

-- AP Invoice - Goods/Services (Services)
SELECT 'APP' AS SOURCE, CONCAT('API G/S ', AI.INVOICE_TYPE_ID, ' - ', AI.SEQUENCE_NO) AS REF_NO,
AC.ACCOUNT_ID, A.ACCOUNT_NAME, A.ACCOUNT_TYPE_ID, (IG.QUANTITY * COALESCE(IG.UP_AMOUNT, 0)) - COALESCE(IG.DISCOUNT, 0) AS LINE_AMOUNT,
AI.AMOUNT + COALESCE(AI.WT_AMOUNT, 0) AS TR_AMOUNT,
(SELECT SUM(APL.PAID_AMOUNT) FROM AP_PAYMENT_LINE APL
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID
INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32) 
AND OTO.OR_TYPE_ID = 24004 AND API.AP_INVOICE_ID = AI.AP_INVOICE_ID) AS PAID_AMOUNT,
COALESCE(AI.WT_AMOUNT, 0) AS WITHHOLDING_TAX
FROM AP_INVOICE_LINE IG 
INNER JOIN AP_INVOICE AI ON AI.AP_INVOICE_ID = IG.AP_INVOICE_ID 
INNER JOIN AP_INVOICE_ACCOUNT AIA ON (AIA.COMPANY_ID = AI.COMPANY_ID AND AIA.DIVISION_ID = AI.DIVISION_ID) 
INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = AIA.ACCOUNT_COMBINATION_ID 
INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID 
INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.FROM_OBJECT_ID = AI.EB_OBJECT_ID
INNER JOIN AP_PAYMENT_LINE AL ON AL.EB_OBJECT_ID = O2O.TO_OBJECT_ID
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = AL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID 
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32)
AND AI.INVOICE_TYPE_ID IN (25, 26, 27, 28, 29, 30) 
AND O2O.OR_TYPE_ID = 24004
AND AI.COMPANY_ID = AP.COMPANY_ID
AND AP.COMPANY_ID = IN_COMPANY_ID
AND AP.DIVISION_ID = IN_DIVISION_ID
AND AP.CHECK_DATE BETWEEN IN_START_DATE AND IN_END_DATE
GROUP BY IG.AP_INVOICE_LINE_ID

UNION ALL

-- AP Invoice - Non-PO
SELECT 'APP' AS SOURCE, CONCAT('API NPO ', AI.INVOICE_TYPE_ID, ' - ', AI.SEQUENCE_NO) AS REF_NO,
AC.ACCOUNT_ID, A.ACCOUNT_NAME, A.ACCOUNT_TYPE_ID, IG.AMOUNT + COALESCE(IG.VAT_AMOUNT, 0) AS LINE_AMOUNT, 
AI.AMOUNT + COALESCE(AI.WT_AMOUNT, 0) AS TR_AMOUNT,
(SELECT SUM(APL.PAID_AMOUNT) FROM AP_PAYMENT_LINE APL
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID
INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32) 
AND OTO.OR_TYPE_ID = 24004 AND API.AP_INVOICE_ID = AI.AP_INVOICE_ID) AS PAID_AMOUNT,
COALESCE(AI.WT_AMOUNT, 0) AS WITHHOLDING_TAX
FROM AP_LINE IG 
INNER JOIN AP_INVOICE AI ON AI.AP_INVOICE_ID = IG.AP_INVOICE_ID 
INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = IG.ACCOUNT_COMBINATION_ID 
INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID 
INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.FROM_OBJECT_ID = AI.EB_OBJECT_ID
INNER JOIN AP_PAYMENT_LINE AL ON AL.EB_OBJECT_ID = O2O.TO_OBJECT_ID
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = AL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID 
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32)
AND AI.INVOICE_TYPE_ID IN (19, 20, 21, 22, 23, 24) 
AND O2O.OR_TYPE_ID = 24004
AND AI.COMPANY_ID = AP.COMPANY_ID
AND AP.COMPANY_ID = IN_COMPANY_ID
AND AP.DIVISION_ID = IN_DIVISION_ID
AND AP.CHECK_DATE BETWEEN IN_START_DATE AND IN_END_DATE
GROUP BY IG.AP_LINE_ID

UNION ALL

-- AP Invoice - Confidential
SELECT 'APP' AS SOURCE, CONCAT('API C ', AI.INVOICE_TYPE_ID, ' - ', AI.SEQUENCE_NO) AS REF_NO,
AC.ACCOUNT_ID, A.ACCOUNT_NAME, A.ACCOUNT_TYPE_ID, IG.AMOUNT + COALESCE(IG.VAT_AMOUNT, 0) AS LINE_AMOUNT,
AI.AMOUNT + COALESCE(AI.WT_AMOUNT, 0) AS TR_AMOUNT,
(SELECT SUM(APL.PAID_AMOUNT) FROM AP_PAYMENT_LINE APL
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID
INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32) 
AND OTO.OR_TYPE_ID = 24004 AND API.AP_INVOICE_ID = AI.AP_INVOICE_ID) AS PAID_AMOUNT,
COALESCE(AI.WT_AMOUNT, 0) AS WITHHOLDING_TAX
FROM AP_LINE IG 
INNER JOIN AP_INVOICE AI ON AI.AP_INVOICE_ID = IG.AP_INVOICE_ID 
INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = IG.ACCOUNT_COMBINATION_ID 
INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID 
INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.FROM_OBJECT_ID = AI.EB_OBJECT_ID
INNER JOIN AP_PAYMENT_LINE AL ON AL.EB_OBJECT_ID = O2O.TO_OBJECT_ID
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = AL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID 
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32)
AND AI.INVOICE_TYPE_ID IN (37, 38, 39, 40, 41, 42) 
AND O2O.OR_TYPE_ID = 24004
AND AI.COMPANY_ID = AP.COMPANY_ID
AND AP.COMPANY_ID = IN_COMPANY_ID
AND AP.DIVISION_ID = IN_DIVISION_ID
AND AP.CHECK_DATE BETWEEN IN_START_DATE AND IN_END_DATE
GROUP BY IG.AP_LINE_ID

UNION ALL

-- AP Invoice - Importation
SELECT 'APP' AS SOURCE, CONCAT('API I ', AI.INVOICE_TYPE_ID, ' - ', AI.SEQUENCE_NO) AS REF_NO,
AC.ACCOUNT_ID, A.ACCOUNT_NAME, A.ACCOUNT_TYPE_ID, IG.AMOUNT + COALESCE(IG.VAT_AMOUNT, 0) AS LINE_AMOUNT,
AI.AMOUNT + COALESCE(AI.WT_AMOUNT, 0) AS TR_AMOUNT,
(SELECT SUM(APL.PAID_AMOUNT) FROM AP_PAYMENT_LINE APL
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID
INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32) 
AND OTO.OR_TYPE_ID = 24004 AND API.AP_INVOICE_ID = AI.AP_INVOICE_ID) AS PAID_AMOUNT,
COALESCE(AI.WT_AMOUNT, 0) AS WITHHOLDING_TAX
FROM AP_LINE IG 
INNER JOIN AP_INVOICE AI ON AI.AP_INVOICE_ID = IG.AP_INVOICE_ID 
INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = IG.ACCOUNT_COMBINATION_ID 
INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID 
INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.FROM_OBJECT_ID = AI.EB_OBJECT_ID
INNER JOIN AP_PAYMENT_LINE AL ON AL.EB_OBJECT_ID = O2O.TO_OBJECT_ID
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = AL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID 
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32)
AND AI.INVOICE_TYPE_ID IN (43, 44, 45, 46, 47, 48) 
AND O2O.OR_TYPE_ID = 24004
AND AI.COMPANY_ID = AP.COMPANY_ID
AND AP.COMPANY_ID = IN_COMPANY_ID
AND AP.DIVISION_ID = IN_DIVISION_ID
AND AP.CHECK_DATE BETWEEN IN_START_DATE AND IN_END_DATE
GROUP BY IG.AP_LINE_ID

UNION ALL

-- AP Invoice - Petty Cash Replenishment
SELECT 'APP' AS SOURCE, CONCAT('PCVR ', AI.INVOICE_TYPE_ID, ' - ', AI.SEQUENCE_NO) AS REF_NO, 
AC.ACCOUNT_ID, A.ACCOUNT_NAME, A.ACCOUNT_TYPE_ID, IG.AMOUNT + COALESCE(IG.VAT_AMOUNT, 0) AS LINE_AMOUNT,
AI.AMOUNT + COALESCE(AI.WT_AMOUNT, 0) AS TR_AMOUNT,
(SELECT SUM(APL.PAID_AMOUNT) FROM AP_PAYMENT_LINE APL
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID
INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32) 
AND OTO.OR_TYPE_ID = 24004 AND API.AP_INVOICE_ID = AI.AP_INVOICE_ID) AS PAID_AMOUNT,
COALESCE(AI.WT_AMOUNT, 0) AS WITHHOLDING_TAX
FROM AP_LINE IG 
INNER JOIN AP_INVOICE AI ON AI.AP_INVOICE_ID = IG.AP_INVOICE_ID 
INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = IG.ACCOUNT_COMBINATION_ID 
INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID 
INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.FROM_OBJECT_ID = AI.EB_OBJECT_ID
INNER JOIN AP_PAYMENT_LINE AL ON AL.EB_OBJECT_ID = O2O.TO_OBJECT_ID
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = AL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID 
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32)
AND AI.INVOICE_TYPE_ID IN (55, 56, 57, 58, 59, 60) 
AND O2O.OR_TYPE_ID = 24004
AND AI.COMPANY_ID = AP.COMPANY_ID
AND AP.COMPANY_ID = IN_COMPANY_ID
AND AP.DIVISION_ID = IN_DIVISION_ID
AND AP.CHECK_DATE BETWEEN IN_START_DATE AND IN_END_DATE
GROUP BY IG.AP_LINE_ID

UNION ALL

-- Supplier advance payments
SELECT 'APP' AS SOURCE, CONCAT('SAP ', SAP.DIVISION_ID, ' - ', SAP.SEQUENCE_NO) AS REF_NO, AC.ACCOUNT_ID,
A.ACCOUNT_NAME, A.ACCOUNT_TYPE_ID, SAP.AMOUNT AS LINE_AMOUNT, SAP.AMOUNT AS TR_AMOUNT,
(SELECT SUM(APL.PAID_AMOUNT) FROM AP_PAYMENT_LINE APL
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID
INNER JOIN SUPPLIER_ADVANCE_PAYMENT SAP1 ON SAP1.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32) AND OTO.OR_TYPE_ID = 24004
AND SAP1.SUPPLIER_ADVANCE_PAYMENT_ID = SAP.SUPPLIER_ADVANCE_PAYMENT_ID) AS PAID_AMOUNT,
0 AS WITHHOLDING_TAX
FROM SUPPLIER_ADVANCE_PAYMENT SAP 
INNER JOIN SUPPLIER_ADV_PAYMENT_ACCOUNT SAPA ON SAPA.COMPANY_ID = SAP.COMPANY_ID
INNER JOIN ACCOUNT_COMBINATION AC ON (AC.ACCOUNT_COMBINATION_ID = SAPA.ACCOUNT_COMBINATION_ID
	AND AC.COMPANY_ID = SAP.COMPANY_ID AND AC.DIVISION_ID = SAP.DIVISION_ID)
INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID 
INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.FROM_OBJECT_ID = SAP.EB_OBJECT_ID
INNER JOIN AP_PAYMENT_LINE AL ON AL.EB_OBJECT_ID = O2O.TO_OBJECT_ID
INNER JOIN AP_PAYMENT AP ON AP.AP_PAYMENT_ID = AL.AP_PAYMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AP.FORM_WORKFLOW_ID 
WHERE FW.CURRENT_STATUS_ID NOT IN (4, 32)
AND O2O.OR_TYPE_ID = 24004
AND SAP.COMPANY_ID = AP.COMPANY_ID
AND AP.COMPANY_ID = IN_COMPANY_ID
AND AP.DIVISION_ID = IN_DIVISION_ID
AND AP.CHECK_DATE BETWEEN IN_START_DATE AND IN_END_DATE
GROUP BY SAP.SUPPLIER_ADVANCE_PAYMENT_ID

UNION ALL

-- AR transaction
SELECT 'AC' AS SOURCE, CONCAT('ART ', ART.AR_TRANSACTION_TYPE_ID, ' - ', ART.SEQUENCE_NO) AS REF_NO,
A.ACCOUNT_ID, A.ACCOUNT_NAME, A.ACCOUNT_TYPE_ID, ARL.AMOUNT + COALESCE(ARL.VAT_AMOUNT, 0) AS LINE_AMOUNT,
ART.AMOUNT + COALESCE(ART.WT_AMOUNT, 0) AS TR_AMOUNT,
COALESCE((SELECT SUM(AL.AMOUNT) FROM AR_RECEIPT_LINE AL
INNER JOIN AR_RECEIPT AR ON AR.AR_RECEIPT_ID = AL.AR_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AR.FORM_WORKFLOW_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = AL.EB_OBJECT_ID 
INNER JOIN AR_TRANSACTION AT ON AT.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND OTO.OR_TYPE_ID = 24006
AND AT.AR_TRANSACTION_ID = ART.AR_TRANSACTION_ID), 0) AS PAID_AMOUNT,
COALESCE(ART.WT_AMOUNT, 0) AS WITHHOLDING_TAX
FROM AR_SERVICE_LINE ARL
INNER JOIN AR_TRANSACTION ART ON ART.AR_TRANSACTION_ID = ARL.AR_TRANSACTION_ID
INNER JOIN SERVICE_SETTING ALS ON ALS.SERVICE_SETTING_ID = ARL.SERVICE_SETTING_ID
INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = ALS.ACCOUNT_COMBINATION_ID
INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID
INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.FROM_OBJECT_ID = ART.EB_OBJECT_ID
INNER JOIN AR_RECEIPT_LINE ARRL ON ARRL.EB_OBJECT_ID = O2O.TO_OBJECT_ID
INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARRL.AR_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4
AND ART.AR_TRANSACTION_TYPE_ID IN (17, 18, 19, 20, 21, 22)
AND ART.COMPANY_ID = ARR.COMPANY_ID
AND O2O.OR_TYPE_ID = 24006
AND ARR.COMPANY_ID = IN_COMPANY_ID
AND ARR.DIVISION_ID = IN_DIVISION_ID
AND ARR.MATURITY_DATE BETWEEN IN_START_DATE AND IN_END_DATE
GROUP BY ARL.AR_SERVICE_LINE_ID

UNION ALL

-- Customer advance payment
SELECT 'AC' AS SOURCE, CONCAT('CAP ', CAP.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID, ' - ', CAP.CAP_NUMBER) AS REF_NO,
A.ACCOUNT_ID, A.ACCOUNT_NAME, A.ACCOUNT_TYPE_ID, CAP.AMOUNT AS LINE_AMOUNT, CAP.AMOUNT AS TR_AMOUNT,
COALESCE((SELECT SUM(AL.AMOUNT) FROM AR_RECEIPT_LINE AL
INNER JOIN AR_RECEIPT AR ON AR.AR_RECEIPT_ID = AL.AR_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AR.FORM_WORKFLOW_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = AL.EB_OBJECT_ID 
INNER JOIN CUSTOMER_ADVANCE_PAYMENT CP ON CP.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND OTO.OR_TYPE_ID = 24006
AND CP.CUSTOMER_ADVANCE_PAYMENT_ID = CAP.CUSTOMER_ADVANCE_PAYMENT_ID), 0) AS PAID_AMOUNT,
0 AS WITHHOLDING_TAX
FROM CUSTOMER_ADVANCE_PAYMENT CAP
INNER JOIN CUSTOMER_ADVANCE_PAYMENT_ACCOUNT CAPA ON (CAPA.COMPANY_ID = CAP.COMPANY_ID AND CAPA.DIVISION_ID = CAP.DIVISION_ID)
INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = CAPA.DEFAULT_CREDIT_AC_ID
INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID
INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.FROM_OBJECT_ID = CAP.EB_OBJECT_ID
INNER JOIN AR_RECEIPT_LINE ARRL ON ARRL.EB_OBJECT_ID = O2O.TO_OBJECT_ID
INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARRL.AR_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4
AND CAP.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID IN (6, 7, 8, 9, 10, 11)
AND CAP.COMPANY_ID = ARR.COMPANY_ID
AND O2O.OR_TYPE_ID = 24006
AND ARR.COMPANY_ID = IN_COMPANY_ID
AND ARR.DIVISION_ID = IN_DIVISION_ID
AND ARR.MATURITY_DATE BETWEEN IN_START_DATE AND IN_END_DATE
GROUP BY CAP.CUSTOMER_ADVANCE_PAYMENT_ID

UNION ALL

-- AR invoice (Non-serialized item)
SELECT 'AC' AS SOURCE, CONCAT('ARI ', ARI.AR_INVOICE_TYPE_ID, ' - ', ARI.SEQUENCE_NO) AS REF_NO,
A.ACCOUNT_ID, A.ACCOUNT_NAME, A.ACCOUNT_TYPE_ID, ARII.AMOUNT + COALESCE(ARII.VAT_AMOUNT, 0) AS LINE_AMOUNT,
ARI.AMOUNT + COALESCE(ARI.WT_AMOUNT, 0) + COALESCE(ARI.RETENTION, 0) AS TR_AMOUNT,
COALESCE((SELECT SUM(AL.AMOUNT) FROM AR_RECEIPT_LINE AL
INNER JOIN AR_RECEIPT AR ON AR.AR_RECEIPT_ID = AL.AR_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AR.FORM_WORKFLOW_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = AL.EB_OBJECT_ID 
INNER JOIN AR_INVOICE ARI1 ON ARI1.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND OTO.OR_TYPE_ID = 24006
AND ARI1.AR_INVOICE_ID = ARI.AR_INVOICE_ID), 0) AS PAID_AMOUNT,
COALESCE(ARI.WT_AMOUNT, 0) AS WITHHOLDING_TAX
FROM AR_INVOICE_ITEM ARII
INNER JOIN AR_INVOICE ARI ON ARI.AR_INVOICE_ID = ARII.AR_INVOICE_ID
INNER JOIN ITEM I ON I.ITEM_ID = ARII.ITEM_ID
INNER JOIN ITEM_CATEGORY_ACCOUNT_SETUP ICAS ON I.ITEM_CATEGORY_ID = ICAS.ITEM_CATEGORY_ID
INNER JOIN ACCOUNT_COMBINATION AC ON (AC.ACCOUNT_COMBINATION_ID = ICAS.SALES_ACCOUNT AND AC.DIVISION_ID = ARI.DIVISION_ID)
INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID
INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.FROM_OBJECT_ID = ARI.EB_OBJECT_ID
INNER JOIN AR_RECEIPT_LINE ARRL ON ARRL.EB_OBJECT_ID = O2O.TO_OBJECT_ID
INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARRL.AR_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4
AND ARI.AR_INVOICE_TYPE_ID IN (3, 4, 5, 6, 7, 8)
AND ARI.COMPANY_ID = ARR.COMPANY_ID
AND O2O.OR_TYPE_ID = 24006
AND ARR.COMPANY_ID = IN_COMPANY_ID
AND ARR.DIVISION_ID = IN_DIVISION_ID
AND ARR.MATURITY_DATE BETWEEN IN_START_DATE AND IN_END_DATE
GROUP BY ARII.AR_INVOICE_ITEM_ID

UNION ALL

-- AR invoice (Serialized item)
SELECT 'AC' AS SOURCE, CONCAT('ARI ', ARI.AR_INVOICE_TYPE_ID, ' - ', ARI.SEQUENCE_NO) AS REF_NO,
A.ACCOUNT_ID, A.ACCOUNT_NAME, A.ACCOUNT_TYPE_ID, ARII.AMOUNT + COALESCE(ARII.VAT_AMOUNT, 0) AS LINE_AMOUNT,
ARI.AMOUNT + COALESCE(ARI.WT_AMOUNT, 0) + COALESCE(ARI.RETENTION, 0) AS TR_AMOUNT,
COALESCE((SELECT SUM(AL.AMOUNT) FROM AR_RECEIPT_LINE AL
INNER JOIN AR_RECEIPT AR ON AR.AR_RECEIPT_ID = AL.AR_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AR.FORM_WORKFLOW_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = AL.EB_OBJECT_ID 
INNER JOIN AR_INVOICE ARI1 ON ARI1.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND OTO.OR_TYPE_ID = 24006
AND ARI1.AR_INVOICE_ID = ARI.AR_INVOICE_ID), 0) AS PAID_AMOUNT,
COALESCE(ARI.WT_AMOUNT, 0) AS WITHHOLDING_TAX
FROM SERIAL_ITEM ARII
INNER JOIN OBJECT_TO_OBJECT OO ON OO.TO_OBJECT_ID = ARII.EB_OBJECT_ID
INNER JOIN AR_INVOICE ARI ON ARI.AR_INVOICE_ID = OO.FROM_OBJECT_ID
INNER JOIN ITEM I ON I.ITEM_ID = ARII.ITEM_ID
INNER JOIN ITEM_CATEGORY_ACCOUNT_SETUP ICAS ON I.ITEM_CATEGORY_ID = ICAS.ITEM_CATEGORY_ID
INNER JOIN ACCOUNT_COMBINATION AC ON (AC.ACCOUNT_COMBINATION_ID = ICAS.SALES_ACCOUNT AND AC.DIVISION_ID = ARI.DIVISION_ID)
INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID
INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.FROM_OBJECT_ID = ARI.EB_OBJECT_ID
INNER JOIN AR_RECEIPT_LINE ARRL ON ARRL.EB_OBJECT_ID = O2O.TO_OBJECT_ID
INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARRL.AR_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4
AND ARI.AR_INVOICE_TYPE_ID IN (3, 4, 5, 6, 7, 8)
AND ARI.COMPANY_ID = ARR.COMPANY_ID
AND OO.OR_TYPE_ID = 12006
AND O2O.OR_TYPE_ID = 24006
AND ARR.COMPANY_ID = IN_COMPANY_ID
AND ARR.DIVISION_ID = IN_DIVISION_ID
AND ARR.MATURITY_DATE BETWEEN IN_START_DATE AND IN_END_DATE
GROUP BY ARII.SERIAL_ITEM_ID

UNION ALL

-- AR invoice (Services)
SELECT 'AC' AS SOURCE, CONCAT('ARI ', ARI.AR_INVOICE_TYPE_ID, ' - ', ARI.SEQUENCE_NO) AS REF_NO,
A.ACCOUNT_ID, A.ACCOUNT_NAME, A.ACCOUNT_TYPE_ID, ARII.AMOUNT + COALESCE(ARII.VAT_AMOUNT, 0) AS LINE_AMOUNT,
ARI.AMOUNT + COALESCE(ARI.WT_AMOUNT, 0) + COALESCE(ARI.RETENTION, 0) AS TR_AMOUNT,
COALESCE((SELECT SUM(AL.AMOUNT) FROM AR_RECEIPT_LINE AL
INNER JOIN AR_RECEIPT AR ON AR.AR_RECEIPT_ID = AL.AR_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AR.FORM_WORKFLOW_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = AL.EB_OBJECT_ID 
INNER JOIN AR_INVOICE ARI1 ON ARI1.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND OTO.OR_TYPE_ID = 24006
AND ARI1.AR_INVOICE_ID = ARI.AR_INVOICE_ID), 0) AS PAID_AMOUNT,
COALESCE(ARI.WT_AMOUNT, 0) AS WITHHOLDING_TAX
FROM AR_INVOICE_LINE ARII
INNER JOIN AR_INVOICE ARI ON ARI.AR_INVOICE_ID = ARII.AR_INVOICE_ID
INNER JOIN SERVICE_SETTING SS ON SS.SERVICE_SETTING_ID = ARII.SERVICE_SETTING_ID
INNER JOIN ACCOUNT_COMBINATION AC ON (AC.ACCOUNT_COMBINATION_ID = SS.ACCOUNT_COMBINATION_ID)
INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID
INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.FROM_OBJECT_ID = ARI.EB_OBJECT_ID
INNER JOIN AR_RECEIPT_LINE ARRL ON ARRL.EB_OBJECT_ID = O2O.TO_OBJECT_ID
INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARRL.AR_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4
AND ARI.AR_INVOICE_TYPE_ID IN (3, 4, 5, 6, 7, 8)
AND ARI.COMPANY_ID = ARR.COMPANY_ID
AND O2O.OR_TYPE_ID = 24006
AND ARR.COMPANY_ID = IN_COMPANY_ID
AND ARR.DIVISION_ID = IN_DIVISION_ID
AND ARR.MATURITY_DATE BETWEEN IN_START_DATE AND IN_END_DATE
GROUP BY ARII.AR_INVOICE_LINE_ID

UNION ALL

-- Project retention
SELECT 'AC' AS SOURCE, CONCAT('PR ', PR.PROJECT_RETENTION_TYPE_ID, ' - ', PR.SEQUENCE_NO) AS REF_NO,
A.ACCOUNT_ID, A.ACCOUNT_NAME, A.ACCOUNT_TYPE_ID, PRL.AMOUNT + COALESCE(PRL.VAT_AMOUNT, 0) AS LINE_AMOUNT,
PR.AMOUNT + COALESCE(PR.WT_AMOUNT) AS TR_AMOUNT,
COALESCE((SELECT SUM(AL.AMOUNT) FROM AR_RECEIPT_LINE AL
INNER JOIN AR_RECEIPT AR ON AR.AR_RECEIPT_ID = AL.AR_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AR.FORM_WORKFLOW_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = AL.EB_OBJECT_ID 
INNER JOIN PROJECT_RETENTION PR1 ON PR1.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND OTO.OR_TYPE_ID = 24006
AND PR1.PROJECT_RETENTION_ID = PR.PROJECT_RETENTION_ID), 0) AS PAID_AMOUNT,
COALESCE(PR.WT_AMOUNT, 0) AS WITHHOLDING_TAX
FROM PROJECT_RETENTION_LINE PRL
INNER JOIN PROJECT_RETENTION PR ON PR.PROJECT_RETENTION_ID = PRL.PROJECT_RETENTION_ID
INNER JOIN PROJECT_RETENTION_ACCOUNT PRA ON (PRA.COMPANY_ID = PR.COMPANY_ID AND PRA.DIVISION_ID = PR.DIVISION_ID)
INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = PRA.DEFAULT_DEBIT_AC_ID
INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID
INNER JOIN OBJECT_TO_OBJECT O2O ON O2O.FROM_OBJECT_ID = PR.EB_OBJECT_ID
INNER JOIN AR_RECEIPT_LINE ARRL ON ARRL.EB_OBJECT_ID = O2O.TO_OBJECT_ID
INNER JOIN AR_RECEIPT ARR ON ARR.AR_RECEIPT_ID = ARRL.AR_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARR.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4
AND PR.PROJECT_RETENTION_TYPE_ID IN (1, 2, 3, 4, 5, 6)
AND PR.COMPANY_ID = ARR.COMPANY_ID
AND O2O.OR_TYPE_ID = 24006
AND ARR.COMPANY_ID = IN_COMPANY_ID
AND ARR.DIVISION_ID = IN_DIVISION_ID
AND ARR.MATURITY_DATE BETWEEN IN_START_DATE AND IN_END_DATE
GROUP BY PRL.PROJECT_RETENTION_LINE_ID

UNION ALL

-- Other receipt
SELECT 'OR' AS SOURCE, CONCAT('OR ', ARM.DIVISION_ID, ' - ', ARM.SEQUENCE_NO) AS REF_NO,
A.ACCOUNT_ID, A.ACCOUNT_NAME, A.ACCOUNT_TYPE_ID, ARML.AMOUNT + COALESCE(ARML.VAT_AMOUNT, 0) AS LINE_AMOUNT,
ARM.AMOUNT + COALESCE(ARM.WT_VAT_AMOUNT, 0) + COALESCE(ARM.WT_AMOUNT, 0) AS TR_AMOUNT,
0 AS PAID_AMOUNT, COALESCE(ARM.WT_AMOUNT, 0) AS WITHHOLDING_TAX
FROM AR_MISCELLANEOUS_LINE ARML
INNER JOIN AR_MISCELLANEOUS ARM ON ARM.AR_MISCELLANEOUS_ID = ARML.AR_MISCELLANEOUS_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARM.FORM_WORKFLOW_ID
INNER JOIN SERVICE_SETTING ALS ON ALS.SERVICE_SETTING_ID = ARML.SERVICE_SETTING_ID
INNER JOIN ACCOUNT_COMBINATION AC ON AC.ACCOUNT_COMBINATION_ID = ALS.ACCOUNT_COMBINATION_ID
INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID
WHERE FW.CURRENT_STATUS_ID != 4
AND ARM.COMPANY_ID = IN_COMPANY_ID
AND ARM.DIVISION_ID = IN_DIVISION_ID
AND ARM.MATURITY_DATE BETWEEN IN_START_DATE AND IN_END_DATE
GROUP BY ARML.AR_MISCELLANEOUS_LINE_ID

) AS CASH_FLOW_TBL;

END //