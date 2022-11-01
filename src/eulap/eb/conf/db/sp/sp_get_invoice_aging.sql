-- Description: Stored procedure for retrieving data for AP Invoice Aging.


Delimiter //
DROP PROCEDURE IF EXISTS GET_INVOICE_AGING;
CREATE PROCEDURE GET_INVOICE_AGING(IN IN_COMPANY_ID INT, IN_DIVISION_ID INT, IN IN_INVOICE_TYPE_ID INT, IN IN_SUPPLIER_ID INT,
IN IN_SUPPLIER_ACCOUNT_ID INT, IN IN_GROUP_BY_OPTION INT, IN IN_AGE_BASIS INT, IN IN_AS_OF_DATE DATE, IN IN_LIMIT_FROM INT, IN IN_LIMIT_TO INT)

BEGIN

SELECT DIVISION, TYPE, ID, SUPPLIER_NAME, SUPPLIER_ACCOUNT, TYPE_SA, INVOICE_NUMBER, TERM,
 SUM(INVOICE_AMT) AS TOTAL_INVOICE, SUM(PAID_AMT) AS TOTAL_PAYMENT, SUM(INVOICE_AMT - PAID_AMT) AS BALANCE,
 SUM(1_30_DAYS) AS 1_30_DAYS, SUM(31_60_DAYS) AS 31_60_DAYS, SUM(61_90_DAYS) AS 61_90_DAYS, SUM(91_120_DAYS),
 SUM(121_150_DAYS) AS 121_150_DAYS, SUM(151_UP) AS 151_UP FROM (

SELECT DIVISION, TYPE, TYPE_ID, ID, AGE_BASIS, SUPPLIER_NAME, SUPPLIER_ACCOUNT, CONCAT(TYPE_ID, ' ', SUPPLIER_ACCOUNT_ID) AS TYPE_SA,
 INVOICE_NUMBER, TERM, SUM(INVOICE_AMT) AS INVOICE_AMT, SUM(PAID_AMT) AS PAID_AMT,
 IF(DATEDIFF(IN_AS_OF_DATE, AGE_BASIS) BETWEEN 1 AND 30, SUM(INVOICE_AMT - PAID_AMT), 0) AS 1_30_DAYS,
 IF(DATEDIFF(IN_AS_OF_DATE, AGE_BASIS) BETWEEN 31 AND 60, SUM(INVOICE_AMT - PAID_AMT), 0) AS 31_60_DAYS, 
 IF(DATEDIFF(IN_AS_OF_DATE, AGE_BASIS) BETWEEN 61 AND 90, SUM(INVOICE_AMT - PAID_AMT), 0) AS 61_90_DAYS, 
 IF(DATEDIFF(IN_AS_OF_DATE, AGE_BASIS) BETWEEN 91 AND 120, SUM(INVOICE_AMT - PAID_AMT), 0) AS 91_120_DAYS,
 IF(DATEDIFF(IN_AS_OF_DATE, AGE_BASIS) BETWEEN 121 AND 150, SUM(INVOICE_AMT - PAID_AMT), 0) AS 121_150_DAYS, 
 IF(DATEDIFF(IN_AS_OF_DATE, AGE_BASIS) >= 151, SUM(INVOICE_AMT - PAID_AMT), 0) AS 151_UP
FROM (
 SELECT
  D.NAME AS DIVISION,
  CONCAT(
	(CASE
		WHEN AP.INVOICE_TYPE_ID BETWEEN 25 AND 30 THEN "API-GS"
		WHEN AP.INVOICE_TYPE_ID BETWEEN 37 AND 42 THEN "API-C"
		WHEN AP.INVOICE_TYPE_ID BETWEEN 19 AND 24 THEN "API-NPO"
		WHEN AP.INVOICE_TYPE_ID BETWEEN 31 AND 36 THEN "RTS"
		WHEN AP.INVOICE_TYPE_ID BETWEEN 49 AND 54 THEN "API-L"
		WHEN AP.INVOICE_TYPE_ID BETWEEN 55 AND 60 THEN "PCR"
		WHEN AP.INVOICE_TYPE_ID BETWEEN 43 AND 48 THEN "API-I"
		END),"",
	(CASE WHEN AP.INVOICE_CLASSIFICATION_ID = 1 THEN "-REG"
		WHEN AP.INVOICE_CLASSIFICATION_ID = 2 THEN "-PRE"
		WHEN AP.INVOICE_CLASSIFICATION_ID = 3 THEN "-DM"
		WHEN AP.INVOICE_CLASSIFICATION_ID = 4 THEN "-CM"
		ELSE "" END)) AS TYPE,
  AP.AP_INVOICE_ID AS ID, AP.INVOICE_TYPE_ID AS TYPE_ID,
  S.NAME AS SUPPLIER_NAME, SA.NAME AS SUPPLIER_ACCOUNT, SA.SUPPLIER_ACCOUNT_ID AS SUPPLIER_ACCOUNT_ID,
  AP.INVOICE_NUMBER AS INVOICE_NUMBER,
  AP.SEQUENCE_NO, TRM.NAME AS TERM,
  (CASE WHEN AP.INVOICE_TYPE_ID  BETWEEN 31 AND 36 THEN -AP.AMOUNT
	ELSE AP.AMOUNT END) AS INVOICE_AMT, 0 AS PAID_AMT,
  (CASE WHEN IN_AGE_BASIS = 1 THEN AP.INVOICE_DATE
	WHEN IN_AGE_BASIS = 2 THEN AP.GL_DATE
	ELSE AP.DUE_DATE END) AS AGE_BASIS
  FROM AP_INVOICE AP
  INNER JOIN DIVISION D ON D.DIVISION_ID = AP.DIVISION_ID
  INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = AP.SUPPLIER_ID
  INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = AP.SUPPLIER_ACCOUNT_ID
  INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID=AP.FORM_WORKFLOW_ID
  LEFT JOIN TERM TRM ON TRM.TERM_ID = AP.TERM_ID
  WHERE FW.IS_COMPLETE = 1
  AND SA.COMPANY_ID = IN_COMPANY_ID
  AND (CASE WHEN IN_DIVISION_ID != -1 THEN AP.DIVISION_ID = IN_DIVISION_ID
			ELSE AP.DIVISION_ID != IN_DIVISION_ID END)
  AND (CASE WHEN IN_INVOICE_TYPE_ID != -1 THEN AP.INVOICE_TYPE_ID = IN_INVOICE_TYPE_ID
  			ELSE AP.INVOICE_TYPE_ID != IN_INVOICE_TYPE_ID END)
  AND (CASE WHEN IN_SUPPLIER_ID != -1 THEN AP.SUPPLIER_ID = IN_SUPPLIER_ID
  			ELSE AP.SUPPLIER_ID != IN_SUPPLIER_ID END)
  AND (CASE WHEN IN_SUPPLIER_ACCOUNT_ID != -1 THEN AP.SUPPLIER_ACCOUNT_ID = IN_SUPPLIER_ACCOUNT_ID
  			ELSE AP.SUPPLIER_ACCOUNT_ID != IN_SUPPLIER_ACCOUNT_ID END)
  AND AP.INVOICE_TYPE_ID NOT IN (8, 9, 13, 14, 15, 16, 17, 18)

UNION ALL

SELECT
  D.NAME AS DIVISION, "PAYMENT" AS TYPE, AP.AP_INVOICE_ID AS ID, AP.INVOICE_TYPE_ID AS TYPE_ID,
  S.NAME AS SUPPLIER_NAME, SA.NAME AS SUPPLIER_ACCOUNT, SA.SUPPLIER_ACCOUNT_ID AS SUPPLIER_ACCOUNT_ID,
  '' AS INVOICE_NUMBER, APP.VOUCHER_NO AS SEQUENCE_NO, '' AS TERM,
  0 AS INVOICE_AMT, APL.PAID_AMOUNT AS PAID_AMT,
  APP.CHECK_DATE AS AGE_BASIS
  FROM AP_PAYMENT_LINE APL
  INNER JOIN AP_PAYMENT APP ON APP.AP_PAYMENT_ID = APL.AP_PAYMENT_ID
  INNER JOIN DIVISION D ON D.DIVISION_ID = APP.DIVISION_ID
  INNER JOIN SUPPLIER S ON S.SUPPLIER_ID = APP.SUPPLIER_ID
  INNER JOIN SUPPLIER_ACCOUNT SA ON SA.SUPPLIER_ACCOUNT_ID = APP.SUPPLIER_ACCOUNT_ID
  INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID=APP.FORM_WORKFLOW_ID
  INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = APL.EB_OBJECT_ID
  INNER JOIN AP_INVOICE AP ON AP.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
  WHERE FW.CURRENT_STATUS_ID NOT IN (1, 4, 32)
  AND APL.PAID_AMOUNT!=0
  AND SA.COMPANY_ID = IN_COMPANY_ID
  AND APP.CHECK_DATE <= IN_AS_OF_DATE
  AND (CASE WHEN IN_DIVISION_ID != -1 THEN AP.DIVISION_ID = IN_DIVISION_ID
			ELSE AP.DIVISION_ID != IN_DIVISION_ID END)
  AND (CASE WHEN IN_INVOICE_TYPE_ID != -1 THEN AP.INVOICE_TYPE_ID = IN_INVOICE_TYPE_ID
			ELSE AP.INVOICE_TYPE_ID != IN_INVOICE_TYPE_ID END)
  AND (CASE WHEN IN_SUPPLIER_ID != -1 THEN AP.SUPPLIER_ID = IN_SUPPLIER_ID
			ELSE AP.SUPPLIER_ID != IN_SUPPLIER_ID END)
  AND (CASE WHEN IN_SUPPLIER_ACCOUNT_ID != -1 THEN AP.SUPPLIER_ACCOUNT_ID = IN_SUPPLIER_ACCOUNT_ID
			ELSE AP.SUPPLIER_ACCOUNT_ID != IN_SUPPLIER_ACCOUNT_ID END)
  AND AP.INVOICE_TYPE_ID NOT IN (8, 9, 13, 14, 15, 16, 17, 18)
 ) AS AGING_INNER_TBL GROUP BY ID 
) AS INVOICE_AGING 
WHERE IN_AS_OF_DATE > AGE_BASIS
GROUP BY CASE WHEN IN_GROUP_BY_OPTION = 1 THEN ID ELSE TYPE_SA END
HAVING (SUM(INVOICE_AMT) - SUM(PAID_AMT)) NOT BETWEEN -0.009 AND 0.009
ORDER BY TYPE_ID, SUPPLIER_NAME, SUPPLIER_ACCOUNT, ID;
END //