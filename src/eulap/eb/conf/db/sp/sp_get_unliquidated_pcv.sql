-- Description: Stored proceedure that will get the unliquidated PCV transactions.

Delimiter //
DROP procedure if exists GET_UNLIQUIDATED_PETTY_CASH_VOUCHER; //
CREATE PROCEDURE GET_UNLIQUIDATED_PETTY_CASH_VOUCHER(IN IN_COMPANY_ID INT, IN IN_DIVISION_ID INT, IN IN_CUSTODIAN_ID INT,
	IN IN_REQUESTOR VARCHAR(100), IN AS_OF_DATE DATE,IN IN_LIMIT_FROM INT, IN IN_LIMIT_TO INT)

BEGIN

SELECT DIVISION, PCV_DATE, PCV_NO, CUSTODIAN, REQUESTOR, AMOUNT,
IF(DATEDIFF(AS_OF_DATE, PCV_DATE) BETWEEN 0 AND 3, AMOUNT, 0) AS 1_3_DAYS,
IF(DATEDIFF(AS_OF_DATE, PCV_DATE) BETWEEN 4 AND 6, AMOUNT, 0) AS 4_6_DAYS, 
IF(DATEDIFF(AS_OF_DATE, PCV_DATE) BETWEEN 7 AND 10, AMOUNT, 0) AS 7_10_DAYS, 
IF(DATEDIFF(AS_OF_DATE, PCV_DATE) > 10, AMOUNT, 0) AS 11_UP FROM (

SELECT D.NAME AS DIVISION, PCV.PCV_DATE, PCV.SEQUENCE_NO AS PCV_NO, CA.CUSTODIAN_NAME AS CUSTODIAN,
PCV.REQUESTOR, PCV.AMOUNT
FROM PETTY_CASH_VOUCHER PCV
INNER JOIN DIVISION D ON D.DIVISION_ID = PCV.DIVISION_ID
INNER JOIN USER_CUSTODIAN UC ON UC.USER_CUSTODIAN_ID = PCV.USER_CUSTODIAN_ID
INNER JOIN CUSTODIAN_ACCOUNT CA ON CA.CUSTODIAN_ACCOUNT_ID = UC.CUSTODIAN_ACCOUNT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PCV.FORM_WORKFLOW_ID
WHERE PCV.COMPANY_ID = IN_COMPANY_ID
AND (CASE WHEN IN_DIVISION_ID != -1 THEN PCV.DIVISION_ID = IN_DIVISION_ID
	ELSE PCV.DIVISION_ID != IN_DIVISION_ID END)
AND (CASE WHEN IN_CUSTODIAN_ID != -1 THEN PCV.USER_CUSTODIAN_ID = IN_CUSTODIAN_ID
	ELSE PCV.USER_CUSTODIAN_ID != IN_CUSTODIAN_ID END)
AND PCV.REQUESTOR LIKE IN_REQUESTOR
AND PCV.PCV_DATE <= AS_OF_DATE
AND FW.IS_COMPLETE
AND PCV.PETTY_CASH_VOUCHER_ID NOT IN (SELECT PCVL.PETTY_CASH_VOUCHER_ID FROM PETTY_CASH_VOUCHER_LIQUIDATION PCVL
INNER JOIN FORM_WORKFLOW FW2 ON FW2.FORM_WORKFLOW_ID = PCVL.FORM_WORKFLOW_ID
WHERE FW2.IS_COMPLETE)
GROUP BY PCV.PETTY_CASH_VOUCHER_ID

) AS UNLIQUIDATED_PCV_AGING
WHERE ROUND(AMOUNT, 2) > 0;
END //
