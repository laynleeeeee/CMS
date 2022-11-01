-- Description: Stored proceedure that will get the sales output report data.

Delimiter //
DROP procedure if exists GET_SALES_OUTPUT;//
CREATE procedure GET_SALES_OUTPUT (IN IN_COMPANY_ID INT, IN IN_DIVISION_ID INT, IN IN_CUSTOMER_ID INT, IN IN_CUSTOMER_ACCT_ID INT,
	IN IN_SALES_PERSONNEL_ID INT, IN IN_PO_NUMBER VARCHAR(50), IN IN_SO_DATE_FROM DATE, IN IN_SO_DATE_TO DATE, IN_DR_DATE_FROM DATE, 
	IN IN_DR_DATE_TO DATE, IN_ARI_DATE_FROM DATE, IN IN_ARI_DATE_TO DATE)
BEGIN

SELECT ID, LINE_ID, MONTH, REQUESTOR, PO_NUMBER, SO_DATE, DELIVERY_DATE, STOCK_CODE, DESCRIPTION, QTY, UOM, UNIT_PRICE, SO_AMOUNT, DR_DATE,
DR_REF, DR_QUANTITY, ARI_DATE, ARI_NUMBER, ARI_QTY , ARI_AMT, SALES_PERSONNEL_ID, CUSTOMER_ID, CUSTOMER_NAME, DR_ID FROM (

-- Unserved SOs
SELECT SO.SALES_ORDER_ID AS ID, SOI.SALES_ORDER_ITEM_ID AS LINE_ID, MONTHNAME(SO.DATE) AS MONTH, '' AS REQUESTOR, SO.DELIVERY_DATE AS DELIVERY_DATE,
SO.PO_NUMBER AS PO_NUMBER, SO.DATE AS SO_DATE, I.STOCK_CODE AS STOCK_CODE, I.DESCRIPTION AS DESCRIPTION, SOI.QUANTITY AS QTY, UOM.NAME AS UOM, 
SOI.GROSS_AMOUNT * SOI.QUANTITY AS UNIT_PRICE, SO.AMOUNT + SO.WT_AMOUNT + SO.WT_VAT_AMOUNT AS SO_AMOUNT, NULL AS DR_DATE, '' AS DR_REF, 0 AS DR_QUANTITY,
NULL AS ARI_DATE, NULL AS ARI_NUMBER, 0 AS ARI_QTY, 0 AS ARI_AMT, NULL AS SALES_PERSONNEL_ID, ARC.AR_CUSTOMER_ID AS CUSTOMER_ID,
ARC.NAME AS CUSTOMER_NAME, NULL AS DR_ID
FROM SALES_ORDER SO
INNER JOIN AR_CUSTOMER ARC ON ARC.AR_CUSTOMER_ID = SO.AR_CUSTOMER_ID
INNER JOIN SALES_ORDER_ITEM SOI ON SOI.SALES_ORDER_ID = SO.SALES_ORDER_ID
INNER JOIN ITEM I ON I.ITEM_ID = SOI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID
WHERE FW.IS_COMPLETE = 1
AND SOI.EB_OBJECT_ID NOT IN (
	SELECT OTO1.FROM_OBJECT_ID FROM OBJECT_TO_OBJECT OTO1
	INNER JOIN DELIVERY_RECEIPT_ITEM DRI ON DRI.EB_OBJECT_ID = OTO1.TO_OBJECT_ID
	INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = DRI.DELIVERY_RECEIPT_ID
	INNER JOIN FORM_WORKFLOW DR_FW ON DR_FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID
	WHERE DR_FW.IS_COMPLETE = 1
	AND OTO1.OR_TYPE_ID = 12005
)
AND SOI.ITEM_ID NOT IN (
	SELECT SI1.ITEM_ID FROM DELIVERY_RECEIPT DR1
	INNER JOIN OBJECT_TO_OBJECT OTO1 ON OTO1.FROM_OBJECT_ID = DR1.EB_OBJECT_ID
	INNER JOIN SERIAL_ITEM SI1 ON SI1.EB_OBJECT_ID = OTO1.TO_OBJECT_ID
	INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = DR1.FORM_WORKFLOW_ID
	AND SI1.ACTIVE = 1 
	AND OTO1.OR_TYPE_ID = 12004
	AND FW1.CURRENT_STATUS_ID != 4
)
AND (CASE WHEN IN_COMPANY_ID != -1 THEN SO.COMPANY_ID = IN_COMPANY_ID
	ELSE SO.COMPANY_ID != IN_COMPANY_ID END)
AND (CASE WHEN IN_DIVISION_ID != -1 THEN SO.DIVISION_ID = IN_DIVISION_ID
	ELSE SO.DIVISION_ID != IN_DIVISION_ID END)
AND (CASE WHEN IN_CUSTOMER_ID IS NOT NULL THEN SO.AR_CUSTOMER_ID = IN_CUSTOMER_ID
	ELSE SO.AR_CUSTOMER_ID IS NOT NULL END)
AND (CASE WHEN IN_CUSTOMER_ACCT_ID != -1 THEN SO.AR_CUSTOMER_ACCOUNT_ID = IN_CUSTOMER_ACCT_ID
	ELSE SO.AR_CUSTOMER_ACCOUNT_ID != IN_CUSTOMER_ACCT_ID END)
AND (CASE WHEN IN_SO_DATE_FROM IS NOT NULL AND IN_SO_DATE_TO IS NOT NULL 
	THEN SO.DATE BETWEEN IN_SO_DATE_FROM AND IN_SO_DATE_TO
	ELSE SO.DATE IS NOT NULL END)
AND (CASE WHEN IN_PO_NUMBER != "" THEN SO.PO_NUMBER = IN_PO_NUMBER
	ELSE 1 = 1 END)
-- If these filters are present then there should not be any result from this query. 
AND (CASE WHEN IN_SALES_PERSONNEL_ID IS NOT NULL THEN 1 != 1 ELSE 1 = 1 END)
AND (CASE WHEN IN_DR_DATE_FROM IS NOT NULL AND IN_DR_DATE_TO IS NOT NULL 
	THEN 1 != 1 ELSE 1 = 1 END)
AND (CASE WHEN IN_ARI_DATE_FROM IS NOT NULL AND IN_ARI_DATE_TO IS NOT NULL 
	THEN 1 != 1 ELSE 1 = 1 END)

UNION ALL

SELECT SO.SALES_ORDER_ID AS ID, SOL.SALES_ORDER_LINE_ID AS LINE_ID, MONTHNAME(SO.DATE) AS MONTH, '' AS REQUESTOR, SO.DELIVERY_DATE AS DELIVERY_DATE, 
SO.PO_NUMBER AS PO_NUMBER, SO.DATE AS SO_DATE,
SS.NAME AS STOCK_CODE, SOL.DESCRIPTION AS DESCRIPTION, SOL.QUANTITY AS QTY, UOM.NAME AS UOM,
SOL.UP_AMOUNT * SOL.QUANTITY AS UNIT_PRICE, SO.AMOUNT + SO.WT_AMOUNT + SO.WT_VAT_AMOUNT AS SO_AMOUNT, NULL AS DR_DATE, '' AS DR_REF, 0 AS DR_QUANTITY,
NULL AS ARI_DATE, NULL AS ARI_NUMBER, 0 AS ARI_QTY, 0 AS ARI_AMT, NULL AS SALES_PERSONNEL_ID,
ARC.AR_CUSTOMER_ID AS CUSTOMER_ID, ARC.NAME AS CUSTOMER_NAME, NULL AS DR_ID
FROM SALES_ORDER SO
INNER JOIN AR_CUSTOMER ARC ON ARC.AR_CUSTOMER_ID = SO.AR_CUSTOMER_ID
INNER JOIN SALES_ORDER_LINE SOL ON SOL.SALES_ORDER_ID = SO.SALES_ORDER_ID
INNER JOIN SERVICE_SETTING SS ON SS.SERVICE_SETTING_ID = SOL.SERVICE_SETTING_ID
LEFT JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = SOL.UNITOFMEASUREMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SO.FORM_WORKFLOW_ID
WHERE FW.IS_COMPLETE = 1
AND SOL.EB_OBJECT_ID NOT IN (
	SELECT SOL.EB_OBJECT_ID FROM SALES_ORDER_LINE SOL
	INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = SOL.SALES_ORDER_ID
	INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = SO.SALES_ORDER_ID
	INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID
	INNER JOIN DELIVERY_RECEIPT_LINE DRL ON (DRL.DELIVERY_RECEIPT_ID = DR.DELIVERY_RECEIPT_ID
		AND DRL.SERVICE_SETTING_ID = SOL.SERVICE_SETTING_ID)
	WHERE FW.IS_COMPLETE = 1
)
AND (CASE WHEN IN_COMPANY_ID != -1 THEN SO.COMPANY_ID = IN_COMPANY_ID
	ELSE SO.COMPANY_ID != IN_COMPANY_ID END)
AND (CASE WHEN IN_DIVISION_ID != -1 THEN SO.DIVISION_ID = IN_DIVISION_ID
	ELSE SO.DIVISION_ID != IN_DIVISION_ID END)
AND (CASE WHEN IN_CUSTOMER_ID IS NOT NULL THEN SO.AR_CUSTOMER_ID = IN_CUSTOMER_ID
	ELSE SO.AR_CUSTOMER_ID IS NOT NULL END)
AND (CASE WHEN IN_CUSTOMER_ACCT_ID != -1 THEN SO.AR_CUSTOMER_ACCOUNT_ID = IN_CUSTOMER_ACCT_ID
	ELSE SO.AR_CUSTOMER_ACCOUNT_ID != IN_CUSTOMER_ACCT_ID END)
AND (CASE WHEN IN_SO_DATE_FROM IS NOT NULL AND IN_SO_DATE_TO IS NOT NULL 
	THEN SO.DATE BETWEEN IN_SO_DATE_FROM AND IN_SO_DATE_TO
	ELSE SO.DATE IS NOT NULL END)
AND (CASE WHEN IN_PO_NUMBER != "" THEN SO.PO_NUMBER = IN_PO_NUMBER
	ELSE 1 = 1 END)
-- If these filters are present then there should not be any result from this query. 
AND (CASE WHEN IN_SALES_PERSONNEL_ID IS NOT NULL THEN 1 != 1 ELSE 1 = 1 END)
AND (CASE WHEN IN_DR_DATE_FROM IS NOT NULL AND IN_DR_DATE_TO IS NOT NULL 
	THEN 1 != 1 ELSE 1 = 1 END)
AND (CASE WHEN IN_ARI_DATE_FROM IS NOT NULL AND IN_ARI_DATE_TO IS NOT NULL 
	THEN 1 != 1 ELSE 1 = 1 END)

UNION ALL

-- Fully and Partially Served SOs
-- SO Items
SELECT SO.SALES_ORDER_ID AS ID, SOI.SALES_ORDER_ITEM_ID AS LINE_ID, MONTHNAME(SO.DATE) AS MONTH, SP.NAME AS REQUESTOR, SO.DELIVERY_DATE AS DELIVERY_DATE,
SO.PO_NUMBER AS PO_NUMBER, SO.DATE AS SO_DATE, I.STOCK_CODE AS STOCK_CODE, I.DESCRIPTION AS DESCRIPTION, SOI.QUANTITY AS QTY, UOM.NAME AS UOM, 
SOI.GROSS_AMOUNT * SOI.QUANTITY AS UNIT_PRICE, SO.AMOUNT + SO.WT_AMOUNT + SO.WT_VAT_AMOUNT AS SO_AMOUNT, DR.DATE AS DR_DATE, DR_REF_NUMBER AS DR_REF, 
(SELECT SUM(DRI1.QUANTITY) FROM DELIVERY_RECEIPT_ITEM DRI1
INNER JOIN DELIVERY_RECEIPT DR1 ON DR1.DELIVERY_RECEIPT_ID = DRI1.DELIVERY_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = DR1.FORM_WORKFLOW_ID
WHERE FW1.IS_COMPLETE AND DRI1.ITEM_ID = DRI.ITEM_ID AND DR1.SALES_ORDER_ID = SO.SALES_ORDER_ID 
AND DR1.DELIVERY_RECEIPT_ID <= DR.DELIVERY_RECEIPT_ID) AS DR_QUANTITY,
NULL AS ARI_DATE, NULL AS ARI_NUMBER, 0 AS ARI_QTY, 0 AS ARI_AMT, SP.SALES_PERSONNEL_ID AS SALES_PERSONNEL_ID, ARC.AR_CUSTOMER_ID AS CUSTOMER_ID,
ARC.NAME AS CUSTOMER_NAME, DR.DELIVERY_RECEIPT_ID AS DR_ID
FROM SALES_ORDER SO
INNER JOIN SALES_ORDER_ITEM SOI ON SOI.SALES_ORDER_ID = SO.SALES_ORDER_ID
INNER JOIN AR_CUSTOMER ARC ON ARC.AR_CUSTOMER_ID = SO.AR_CUSTOMER_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = SOI.EB_OBJECT_ID
INNER JOIN DELIVERY_RECEIPT_ITEM DRI ON DRI.EB_OBJECT_ID = OTO.TO_OBJECT_ID
INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = DRI.DELIVERY_RECEIPT_ID
INNER JOIN ITEM I ON I.ITEM_ID = SOI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
LEFT JOIN SALES_PERSONNEL SP ON SP.SALES_PERSONNEL_ID = DR.SALES_PERSONNEL_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID
WHERE FW.IS_COMPLETE = 1
AND (CASE WHEN IN_COMPANY_ID != -1 THEN SO.COMPANY_ID = IN_COMPANY_ID
	ELSE SO.COMPANY_ID != IN_COMPANY_ID END)
AND (CASE WHEN IN_DIVISION_ID != -1 THEN SO.DIVISION_ID = IN_DIVISION_ID
	ELSE SO.DIVISION_ID != IN_DIVISION_ID END)
AND (CASE WHEN IN_CUSTOMER_ID IS NOT NULL THEN SO.AR_CUSTOMER_ID = IN_CUSTOMER_ID
	ELSE SO.AR_CUSTOMER_ID IS NOT NULL END)
AND (CASE WHEN IN_CUSTOMER_ACCT_ID != -1 THEN SO.AR_CUSTOMER_ACCOUNT_ID = IN_CUSTOMER_ACCT_ID
	ELSE SO.AR_CUSTOMER_ACCOUNT_ID != IN_CUSTOMER_ACCT_ID END)
AND (CASE WHEN IN_SALES_PERSONNEL_ID IS NOT NULL THEN DR.SALES_PERSONNEL_ID = IN_SALES_PERSONNEL_ID
	ELSE 1 = 1 END)
AND (CASE WHEN IN_SO_DATE_FROM IS NOT NULL AND IN_SO_DATE_TO IS NOT NULL 
	THEN SO.DATE BETWEEN IN_SO_DATE_FROM AND IN_SO_DATE_TO
	ELSE SO.DATE IS NOT NULL END)
AND (CASE WHEN IN_DR_DATE_FROM IS NOT NULL AND IN_DR_DATE_TO IS NOT NULL 
	THEN DR.DATE BETWEEN IN_DR_DATE_FROM AND IN_DR_DATE_TO
	ELSE DR.DATE IS NOT NULL END)
AND (CASE WHEN IN_PO_NUMBER != "" THEN SO.PO_NUMBER = IN_PO_NUMBER
	ELSE 1 = 1 END)
-- If these filters are present then there should not be any result from this query. 
AND (CASE WHEN IN_ARI_DATE_FROM IS NOT NULL AND IN_ARI_DATE_TO IS NOT NULL 
	THEN 1 != 1 ELSE 1 = 1 END)

UNION ALL

SELECT SO.SALES_ORDER_ID AS ID, SOI.SALES_ORDER_ITEM_ID AS LINE_ID, MONTHNAME(SO.DATE) AS MONTH, SP.NAME AS REQUESTOR, SO.DELIVERY_DATE AS DELIVERY_DATE,
SO.PO_NUMBER AS PO_NUMBER, SO.DATE AS SO_DATE, I.STOCK_CODE AS STOCK_CODE, I.DESCRIPTION AS DESCRIPTION, SOI.QUANTITY AS QTY, UOM.NAME AS UOM, 
SOI.GROSS_AMOUNT * SOI.QUANTITY AS UNIT_PRICE, SO.AMOUNT + SO.WT_AMOUNT + SO.WT_VAT_AMOUNT AS SO_AMOUNT, DR.DATE AS DR_DATE, DR_REF_NUMBER AS DR_REF,
(SELECT SUM(SI1.QUANTITY) FROM SERIAL_ITEM SI1
INNER JOIN OBJECT_TO_OBJECT OTO1 ON OTO1.TO_OBJECT_ID = SI1.EB_OBJECT_ID
INNER JOIN DELIVERY_RECEIPT DR1 ON DR1.EB_OBJECT_ID = OTO1.FROM_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = DR1.FORM_WORKFLOW_ID
WHERE FW1.IS_COMPLETE AND SI1.ITEM_ID = SI.ITEM_ID AND DR1.SALES_ORDER_ID = SO.SALES_ORDER_ID 
AND DR1.DELIVERY_RECEIPT_ID <= DR.DELIVERY_RECEIPT_ID AND SI1.ACTIVE = 1 AND OTO1.OR_TYPE_ID = 12004) AS DR_QUANTITY,
NULL AS ARI_DATE, NULL AS ARI_NUMBER, 0 AS ARI_QTY, 0 AS ARI_AMT, SP.SALES_PERSONNEL_ID AS SALES_PERSONNEL_ID, ARC.AR_CUSTOMER_ID AS CUSTOMER_ID,
ARC.NAME AS CUSTOMER_NAME, DR.DELIVERY_RECEIPT_ID AS DR_ID
FROM SALES_ORDER SO
INNER JOIN SALES_ORDER_ITEM SOI ON SOI.SALES_ORDER_ID = SO.SALES_ORDER_ID
INNER JOIN AR_CUSTOMER ARC ON ARC.AR_CUSTOMER_ID = SO.AR_CUSTOMER_ID
INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = SO.SALES_ORDER_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = DR.EB_OBJECT_ID
INNER JOIN SERIAL_ITEM SI ON SI.EB_OBJECT_ID = OTO.TO_OBJECT_ID
INNER JOIN ITEM I ON I.ITEM_ID = SOI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
LEFT JOIN SALES_PERSONNEL SP ON SP.SALES_PERSONNEL_ID = DR.SALES_PERSONNEL_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID
WHERE FW.IS_COMPLETE = 1
AND SI.ACTIVE = 1
AND OTO.OR_TYPE_ID = 12004
AND SOI.ITEM_ID IN (SELECT ITEM_ID FROM SERIAL_ITEM_SETUP WHERE SERIALIZED_ITEM = 1)
AND (CASE WHEN IN_COMPANY_ID != -1 THEN SO.COMPANY_ID = IN_COMPANY_ID
	ELSE SO.COMPANY_ID != IN_COMPANY_ID END)
AND (CASE WHEN IN_DIVISION_ID != -1 THEN SO.DIVISION_ID = IN_DIVISION_ID
	ELSE SO.DIVISION_ID != IN_DIVISION_ID END)
AND (CASE WHEN IN_CUSTOMER_ID IS NOT NULL THEN SO.AR_CUSTOMER_ID = IN_CUSTOMER_ID
	ELSE SO.AR_CUSTOMER_ID IS NOT NULL END)
AND (CASE WHEN IN_CUSTOMER_ACCT_ID != -1 THEN SO.AR_CUSTOMER_ACCOUNT_ID = IN_CUSTOMER_ACCT_ID
	ELSE SO.AR_CUSTOMER_ACCOUNT_ID != IN_CUSTOMER_ACCT_ID END)
AND (CASE WHEN IN_SALES_PERSONNEL_ID IS NOT NULL THEN DR.SALES_PERSONNEL_ID = IN_SALES_PERSONNEL_ID
	ELSE 1 = 1 END)
AND (CASE WHEN IN_SO_DATE_FROM IS NOT NULL AND IN_SO_DATE_TO IS NOT NULL 
	THEN SO.DATE BETWEEN IN_SO_DATE_FROM AND IN_SO_DATE_TO
	ELSE SO.DATE IS NOT NULL END)
AND (CASE WHEN IN_DR_DATE_FROM IS NOT NULL AND IN_DR_DATE_TO IS NOT NULL 
	THEN DR.DATE BETWEEN IN_DR_DATE_FROM AND IN_DR_DATE_TO
	ELSE DR.DATE IS NOT NULL END)
AND (CASE WHEN IN_PO_NUMBER != "" THEN SO.PO_NUMBER = IN_PO_NUMBER
	ELSE 1 = 1 END)
-- If these filters are present then there should not be any result from this query. 
AND (CASE WHEN IN_ARI_DATE_FROM IS NOT NULL AND IN_ARI_DATE_TO IS NOT NULL 
	THEN 1 != 1 ELSE 1 = 1 END)

UNION ALL

SELECT SO.SALES_ORDER_ID AS ID, SOI.SALES_ORDER_ITEM_ID AS LINE_ID, MONTHNAME(SO.DATE) AS MONTH, SP.NAME AS REQUESTOR, SO.DELIVERY_DATE AS DELIVERY_DATE, 
SO.PO_NUMBER AS PO_NUMBER, SO.DATE AS SO_DATE,
I.STOCK_CODE AS STOCK_CODE, I.DESCRIPTION AS DESCRIPTION, SOI.QUANTITY AS QTY, UOM.NAME AS UOM, 
SOI.GROSS_AMOUNT * SOI.QUANTITY AS UNIT_PRICE, SO.AMOUNT + SO.WT_AMOUNT + SO.WT_VAT_AMOUNT AS SO_AMOUNT, DR.DATE AS DR_DATE, DR_REF_NUMBER AS DR_REF,
(SELECT SUM(DRI1.QUANTITY) FROM DELIVERY_RECEIPT_ITEM DRI1
INNER JOIN DELIVERY_RECEIPT DR1 ON DR1.DELIVERY_RECEIPT_ID = DRI1.DELIVERY_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = DR1.FORM_WORKFLOW_ID
WHERE FW1.IS_COMPLETE AND DRI1.ITEM_ID = DRI.ITEM_ID AND DR1.SALES_ORDER_ID = SO.SALES_ORDER_ID 
AND DR1.DELIVERY_RECEIPT_ID <= DR.DELIVERY_RECEIPT_ID) AS DR_QUANTITY,
ARI.DATE AS ARI_DATE, ARI.SEQUENCE_NO AS ARI_NUMBER, ARII.QUANTITY AS ARI_QTY, ARII.SRP * ARII.QUANTITY AS ARI_AMT,
SP.SALES_PERSONNEL_ID AS SALES_PERSONNEL_ID, ARC.AR_CUSTOMER_ID AS CUSTOMER_ID, ARC.NAME AS CUSTOMER_NAME, DR.DELIVERY_RECEIPT_ID AS DR_ID
FROM SALES_ORDER SO
INNER JOIN AR_CUSTOMER ARC ON ARC.AR_CUSTOMER_ID = SO.AR_CUSTOMER_ID
INNER JOIN SALES_ORDER_ITEM SOI ON SOI.SALES_ORDER_ID = SO.SALES_ORDER_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = SOI.EB_OBJECT_ID
INNER JOIN DELIVERY_RECEIPT_ITEM DRI ON DRI.EB_OBJECT_ID = OTO.TO_OBJECT_ID
INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = DRI.DELIVERY_RECEIPT_ID
INNER JOIN OBJECT_TO_OBJECT OTO_ARII ON OTO_ARII.FROM_OBJECT_ID = DRI.EB_OBJECT_ID
INNER JOIN AR_INVOICE_ITEM ARII ON ARII.EB_OBJECT_ID = OTO_ARII.TO_OBJECT_ID
INNER JOIN AR_INVOICE ARI ON ARI.AR_INVOICE_ID = ARII.AR_INVOICE_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ARI.FORM_WORKFLOW_ID
INNER JOIN ITEM I ON I.ITEM_ID = SOI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
LEFT JOIN SALES_PERSONNEL SP ON SP.SALES_PERSONNEL_ID = DR.SALES_PERSONNEL_ID
WHERE FW.IS_COMPLETE = 1
AND (CASE WHEN IN_COMPANY_ID != -1 THEN SO.COMPANY_ID = IN_COMPANY_ID
	ELSE SO.COMPANY_ID != IN_COMPANY_ID END)
AND (CASE WHEN IN_DIVISION_ID != -1 THEN SO.DIVISION_ID = IN_DIVISION_ID
	ELSE SO.DIVISION_ID != IN_DIVISION_ID END)
AND (CASE WHEN IN_CUSTOMER_ID IS NOT NULL THEN SO.AR_CUSTOMER_ID = IN_CUSTOMER_ID
	ELSE SO.AR_CUSTOMER_ID IS NOT NULL END)
AND (CASE WHEN IN_CUSTOMER_ACCT_ID != -1 THEN SO.AR_CUSTOMER_ACCOUNT_ID = IN_CUSTOMER_ACCT_ID
	ELSE SO.AR_CUSTOMER_ACCOUNT_ID != IN_CUSTOMER_ACCT_ID END)
AND (CASE WHEN IN_SALES_PERSONNEL_ID IS NOT NULL THEN DR.SALES_PERSONNEL_ID = IN_SALES_PERSONNEL_ID
	ELSE 1 = 1 END)
AND (CASE WHEN IN_SO_DATE_FROM IS NOT NULL AND IN_SO_DATE_TO IS NOT NULL 
	THEN SO.DATE BETWEEN IN_SO_DATE_FROM AND IN_SO_DATE_TO
	ELSE SO.DATE IS NOT NULL END)
AND (CASE WHEN IN_DR_DATE_FROM IS NOT NULL AND IN_DR_DATE_TO IS NOT NULL 
	THEN DR.DATE BETWEEN IN_DR_DATE_FROM AND IN_DR_DATE_TO
	ELSE DR.DATE IS NOT NULL END)
AND (CASE WHEN IN_ARI_DATE_FROM IS NOT NULL AND IN_ARI_DATE_TO IS NOT NULL 
	THEN ARI.DATE BETWEEN IN_ARI_DATE_FROM AND IN_ARI_DATE_TO
	ELSE ARI.DATE IS NOT NULL END)
AND (CASE WHEN IN_PO_NUMBER != "" THEN SO.PO_NUMBER = IN_PO_NUMBER
	ELSE 1 = 1 END)

UNION ALL

SELECT SO.SALES_ORDER_ID AS ID, SOI.SALES_ORDER_ITEM_ID AS LINE_ID, MONTHNAME(SO.DATE) AS MONTH, SP.NAME AS REQUESTOR, SO.DELIVERY_DATE AS DELIVERY_DATE, 
SO.PO_NUMBER AS PO_NUMBER, SO.DATE AS SO_DATE,
I.STOCK_CODE AS STOCK_CODE, I.DESCRIPTION AS DESCRIPTION, SOI.QUANTITY AS QTY, UOM.NAME AS UOM, 
SOI.GROSS_AMOUNT * SOI.QUANTITY AS UNIT_PRICE, SO.AMOUNT + SO.WT_AMOUNT + SO.WT_VAT_AMOUNT AS SO_AMOUNT, DR.DATE AS DR_DATE, DR_REF_NUMBER AS DR_REF,
(SELECT SUM(SI1.QUANTITY) FROM SERIAL_ITEM SI1
	INNER JOIN OBJECT_TO_OBJECT OTO1 ON OTO1.TO_OBJECT_ID = SI1.EB_OBJECT_ID
	INNER JOIN DELIVERY_RECEIPT DR1 ON DR1.EB_OBJECT_ID = OTO1.FROM_OBJECT_ID
	INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = DR1.FORM_WORKFLOW_ID
	WHERE FW1.IS_COMPLETE AND SI1.ITEM_ID = SI.ITEM_ID AND DR1.SALES_ORDER_ID = SO.SALES_ORDER_ID 
	AND DR1.DELIVERY_RECEIPT_ID <= DR.DELIVERY_RECEIPT_ID AND SI1.ACTIVE = 1
	AND OTO1.OR_TYPE_ID = 12004) AS DR_QUANTITY,
ARI.DATE AS ARI_DATE, ARI.SEQUENCE_NO AS ARI_NUMBER, SI.QUANTITY AS ARI_QTY, SI.SRP * SI.QUANTITY AS ARI_AMT,
SP.SALES_PERSONNEL_ID AS SALES_PERSONNEL_ID, ARC.AR_CUSTOMER_ID AS CUSTOMER_ID, ARC.NAME AS CUSTOMER_NAME, DR.DELIVERY_RECEIPT_ID AS DR_ID
FROM SALES_ORDER SO
INNER JOIN SALES_ORDER_ITEM SOI ON SOI.SALES_ORDER_ID = SO.SALES_ORDER_ID
INNER JOIN ITEM I ON I.ITEM_ID = SOI.ITEM_ID
INNER JOIN SERIAL_ITEM_SETUP SIS ON SIS.ITEM_ID = SOI.ITEM_ID
INNER JOIN AR_CUSTOMER ARC ON ARC.AR_CUSTOMER_ID = SO.AR_CUSTOMER_ID
INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = SO.SALES_ORDER_ID
INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID
INNER JOIN AR_INVOICE ARI ON FIND_IN_SET(DR.DELIVERY_RECEIPT_ID, (REPLACE(ARI.DR_REFERENCE_IDS, ' ', '')))
INNER JOIN FORM_WORKFLOW FW2 ON FW2.FORM_WORKFLOW_ID = ARI.FORM_WORKFLOW_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = ARI.EB_OBJECT_ID
INNER JOIN SERIAL_ITEM SI ON SI.EB_OBJECT_ID = OTO.TO_OBJECT_ID
INNER JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
LEFT JOIN SALES_PERSONNEL SP ON SP.SALES_PERSONNEL_ID = DR.SALES_PERSONNEL_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID
WHERE FW1.IS_COMPLETE = 1
AND FW2.IS_COMPLETE = 1
AND SI.ACTIVE = 1
AND SIS.SERIALIZED_ITEM = 1
AND OTO.OR_TYPE_ID = 12006
AND (CASE WHEN IN_COMPANY_ID != -1 THEN SO.COMPANY_ID = IN_COMPANY_ID
	ELSE SO.COMPANY_ID != IN_COMPANY_ID END)
AND (CASE WHEN IN_DIVISION_ID != -1 THEN SO.DIVISION_ID = IN_DIVISION_ID
	ELSE SO.DIVISION_ID != IN_DIVISION_ID END)
AND (CASE WHEN IN_CUSTOMER_ID IS NOT NULL THEN SO.AR_CUSTOMER_ID = IN_CUSTOMER_ID
	ELSE SO.AR_CUSTOMER_ID IS NOT NULL END)
AND (CASE WHEN IN_CUSTOMER_ACCT_ID != -1 THEN SO.AR_CUSTOMER_ACCOUNT_ID = IN_CUSTOMER_ACCT_ID
	ELSE SO.AR_CUSTOMER_ACCOUNT_ID != IN_CUSTOMER_ACCT_ID END)
AND (CASE WHEN IN_SALES_PERSONNEL_ID IS NOT NULL THEN DR.SALES_PERSONNEL_ID = IN_SALES_PERSONNEL_ID
	ELSE 1 = 1 END)
AND (CASE WHEN IN_SO_DATE_FROM IS NOT NULL AND IN_SO_DATE_TO IS NOT NULL 
	THEN SO.DATE BETWEEN IN_SO_DATE_FROM AND IN_SO_DATE_TO
	ELSE SO.DATE IS NOT NULL END)
AND (CASE WHEN IN_DR_DATE_FROM IS NOT NULL AND IN_DR_DATE_TO IS NOT NULL 
	THEN DR.DATE BETWEEN IN_DR_DATE_FROM AND IN_DR_DATE_TO
	ELSE DR.DATE IS NOT NULL END)
AND (CASE WHEN IN_ARI_DATE_FROM IS NOT NULL AND IN_ARI_DATE_TO IS NOT NULL 
	THEN ARI.DATE BETWEEN IN_ARI_DATE_FROM AND IN_ARI_DATE_TO
	ELSE ARI.DATE IS NOT NULL END)
AND (CASE WHEN IN_PO_NUMBER != "" THEN SO.PO_NUMBER = IN_PO_NUMBER
	ELSE 1 = 1 END)

UNION ALL

-- SO Lines
SELECT SO.SALES_ORDER_ID AS ID, SOL.SALES_ORDER_LINE_ID AS LINE_ID, MONTHNAME(SO.DATE) AS MONTH, SP.NAME AS REQUESTOR, SO.DELIVERY_DATE AS DELIVERY_DATE, 
SO.PO_NUMBER AS PO_NUMBER, SO.DATE AS SO_DATE,
SS.NAME AS STOCK_CODE, SOL.DESCRIPTION AS DESCRIPTION, SOL.QUANTITY AS QTY, UOM.NAME AS UOM,
SOL.UP_AMOUNT * SOL.QUANTITY AS UNIT_PRICE, SO.AMOUNT + SO.WT_AMOUNT + SO.WT_VAT_AMOUNT AS SO_AMOUNT, DR.DATE AS DR_DATE, DR_REF_NUMBER AS DR_REF,
(SELECT SUM(DRL1.QUANTITY) FROM DELIVERY_RECEIPT_LINE DRL1
INNER JOIN DELIVERY_RECEIPT DR1 ON DR1.DELIVERY_RECEIPT_ID = DRL1.DELIVERY_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = DR1.FORM_WORKFLOW_ID
WHERE FW1.IS_COMPLETE AND DRL1.SERVICE_SETTING_ID = DRL.SERVICE_SETTING_ID
AND DR1.SALES_ORDER_ID = DR.SALES_ORDER_ID AND DR1.DELIVERY_RECEIPT_ID <= DR.DELIVERY_RECEIPT_ID) AS DR_QUANTITY,
NULL AS ARI_DATE, NULL AS ARI_NUMBER, 0 AS ARI_QTY, 0 AS ARI_AMT, SP.SALES_PERSONNEL_ID AS SALES_PERSONNEL_ID,
ARC.AR_CUSTOMER_ID AS CUSTOMER_ID, ARC.NAME AS CUSTOMER_NAME, DR.DELIVERY_RECEIPT_ID AS DR_ID
FROM SALES_ORDER SO
INNER JOIN AR_CUSTOMER ARC ON ARC.AR_CUSTOMER_ID = SO.AR_CUSTOMER_ID
INNER JOIN SALES_ORDER_LINE SOL ON SOL.SALES_ORDER_ID = SO.SALES_ORDER_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = SOL.EB_OBJECT_ID
INNER JOIN DELIVERY_RECEIPT_LINE DRL ON DRL.EB_OBJECT_ID = OTO.TO_OBJECT_ID
INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = DRL.DELIVERY_RECEIPT_ID
INNER JOIN SERVICE_SETTING SS ON SS.SERVICE_SETTING_ID = SOL.SERVICE_SETTING_ID
LEFT JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = SOL.UNITOFMEASUREMENT_ID
INNER JOIN SALES_PERSONNEL SP ON SP.SALES_PERSONNEL_ID = DR.SALES_PERSONNEL_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID
WHERE FW.IS_COMPLETE = 1
AND (CASE WHEN IN_COMPANY_ID != -1 THEN SO.COMPANY_ID = IN_COMPANY_ID
	ELSE SO.COMPANY_ID != IN_COMPANY_ID END)
AND (CASE WHEN IN_DIVISION_ID != -1 THEN SO.DIVISION_ID = IN_DIVISION_ID
	ELSE SO.DIVISION_ID != IN_DIVISION_ID END)
AND (CASE WHEN IN_CUSTOMER_ID IS NOT NULL THEN SO.AR_CUSTOMER_ID = IN_CUSTOMER_ID
	ELSE SO.AR_CUSTOMER_ID IS NOT NULL END)
AND (CASE WHEN IN_CUSTOMER_ACCT_ID != -1 THEN SO.AR_CUSTOMER_ACCOUNT_ID = IN_CUSTOMER_ACCT_ID
	ELSE SO.AR_CUSTOMER_ACCOUNT_ID != IN_CUSTOMER_ACCT_ID END)
AND (CASE WHEN IN_SALES_PERSONNEL_ID IS NOT NULL THEN DR.SALES_PERSONNEL_ID = IN_SALES_PERSONNEL_ID
	ELSE 1 = 1 END)
AND (CASE WHEN IN_SO_DATE_FROM IS NOT NULL AND IN_SO_DATE_TO IS NOT NULL 
	THEN SO.DATE BETWEEN IN_SO_DATE_FROM AND IN_SO_DATE_TO
	ELSE SO.DATE IS NOT NULL END)
AND (CASE WHEN IN_DR_DATE_FROM IS NOT NULL AND IN_DR_DATE_TO IS NOT NULL 
	THEN DR.DATE BETWEEN IN_DR_DATE_FROM AND IN_DR_DATE_TO
	ELSE DR.DATE IS NOT NULL END)
AND (CASE WHEN IN_PO_NUMBER != "" THEN SO.PO_NUMBER = IN_PO_NUMBER
	ELSE 1 = 1 END)
-- If these filters are present then there should not be any result from this query. 
AND (CASE WHEN IN_ARI_DATE_FROM IS NOT NULL AND IN_ARI_DATE_TO IS NOT NULL 
	THEN 1 != 1 ELSE 1 = 1 END)

UNION ALL

SELECT DISTINCT SO.SALES_ORDER_ID AS ID, SOL.SALES_ORDER_LINE_ID AS LINE_ID, MONTHNAME(SO.DATE) AS MONTH, SP.NAME AS REQUESTOR, SO.DELIVERY_DATE AS DELIVERY_DATE, 
SO.PO_NUMBER AS PO_NUMBER, SO.DATE AS SO_DATE,
SS.NAME AS STOCK_CODE, SOL.DESCRIPTION AS DESCRIPTION, SOL.QUANTITY AS QTY, UOM.NAME AS UOM,
SOL.UP_AMOUNT * SOL.QUANTITY AS UNIT_PRICE, SO.AMOUNT + SO.WT_AMOUNT + SO.WT_VAT_AMOUNT AS SO_AMOUNT, DR.DATE AS DR_DATE, DR_REF_NUMBER AS DR_REF, 
(SELECT SUM(DRL1.QUANTITY) FROM DELIVERY_RECEIPT_LINE DRL1
	INNER JOIN DELIVERY_RECEIPT DR1 ON DR1.DELIVERY_RECEIPT_ID = DRL1.DELIVERY_RECEIPT_ID
	INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = DR1.FORM_WORKFLOW_ID
	WHERE FW1.IS_COMPLETE AND DRL1.SERVICE_SETTING_ID = DRL.SERVICE_SETTING_ID
	AND DR1.SALES_ORDER_ID = DR.SALES_ORDER_ID AND DR1.DELIVERY_RECEIPT_ID <= DR.DELIVERY_RECEIPT_ID) AS DR_QUANTITY,
ARI.DATE AS ARI_DATE, ARI.SEQUENCE_NO AS ARI_NUMBER, ARL.QUANTITY AS ARI_QTY, ARL.UP_AMOUNT * ARL.QUANTITY AS ARI_AMT, SP.SALES_PERSONNEL_ID AS SALES_PERSONNEL_ID,
ARC.AR_CUSTOMER_ID AS CUSTOMER_ID, ARC.NAME AS CUSTOMER_NAME, DR.DELIVERY_RECEIPT_ID AS DR_ID
FROM SALES_ORDER SO
INNER JOIN AR_CUSTOMER ARC ON ARC.AR_CUSTOMER_ID = SO.AR_CUSTOMER_ID
INNER JOIN SALES_ORDER_LINE SOL ON SOL.SALES_ORDER_ID = SO.SALES_ORDER_ID
INNER JOIN SERVICE_SETTING SS ON SS.SERVICE_SETTING_ID = SOL.SERVICE_SETTING_ID
INNER JOIN DELIVERY_RECEIPT DR ON DR.SALES_ORDER_ID = SO.SALES_ORDER_ID
INNER JOIN FORM_WORKFLOW FW1 ON FW1.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID
INNER JOIN DELIVERY_RECEIPT_LINE DRL ON (DRL.DELIVERY_RECEIPT_ID = DR.DELIVERY_RECEIPT_ID
	AND DRL.SERVICE_SETTING_ID = SOL.SERVICE_SETTING_ID)
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.FROM_OBJECT_ID = DRL.EB_OBJECT_ID
INNER JOIN AR_INVOICE_LINE ARL ON ARL.EB_OBJECT_ID = OTO.TO_OBJECT_ID
INNER JOIN AR_INVOICE ARI ON ARI.AR_INVOICE_ID = ARL.AR_INVOICE_ID
INNER JOIN FORM_WORKFLOW FW2 ON FW2.FORM_WORKFLOW_ID = ARI.FORM_WORKFLOW_ID
LEFT JOIN UNIT_MEASUREMENT UOM ON UOM.UNITOFMEASUREMENT_ID = SOL.UNITOFMEASUREMENT_ID
INNER JOIN SALES_PERSONNEL SP ON SP.SALES_PERSONNEL_ID = DR.SALES_PERSONNEL_ID
WHERE FW1.IS_COMPLETE = 1
AND FW2.IS_COMPLETE = 1
AND OTO.OR_TYPE_ID = 12007
AND (CASE WHEN IN_COMPANY_ID != -1 THEN SO.COMPANY_ID = IN_COMPANY_ID
	ELSE SO.COMPANY_ID != IN_COMPANY_ID END)
AND (CASE WHEN IN_DIVISION_ID != -1 THEN SO.DIVISION_ID = IN_DIVISION_ID
	ELSE SO.DIVISION_ID != IN_DIVISION_ID END)
AND (CASE WHEN IN_CUSTOMER_ID IS NOT NULL THEN SO.AR_CUSTOMER_ID = IN_CUSTOMER_ID
	ELSE SO.AR_CUSTOMER_ID IS NOT NULL END)
AND (CASE WHEN IN_CUSTOMER_ACCT_ID != -1 THEN SO.AR_CUSTOMER_ACCOUNT_ID = IN_CUSTOMER_ACCT_ID
	ELSE SO.AR_CUSTOMER_ACCOUNT_ID != IN_CUSTOMER_ACCT_ID END)
AND (CASE WHEN IN_SALES_PERSONNEL_ID IS NOT NULL THEN DR.SALES_PERSONNEL_ID = IN_SALES_PERSONNEL_ID
	ELSE 1 = 1 END)
AND (CASE WHEN IN_SO_DATE_FROM IS NOT NULL AND IN_SO_DATE_TO IS NOT NULL 
	THEN SO.DATE BETWEEN IN_SO_DATE_FROM AND IN_SO_DATE_TO
	ELSE SO.DATE IS NOT NULL END)
AND (CASE WHEN IN_DR_DATE_FROM IS NOT NULL AND IN_DR_DATE_TO IS NOT NULL 
	THEN DR.DATE BETWEEN IN_DR_DATE_FROM AND IN_DR_DATE_TO
	ELSE DR.DATE IS NOT NULL END)
AND (CASE WHEN IN_ARI_DATE_FROM IS NOT NULL AND IN_ARI_DATE_TO IS NOT NULL 
	THEN ARI.DATE BETWEEN IN_ARI_DATE_FROM AND IN_ARI_DATE_TO
	ELSE ARI.DATE IS NOT NULL END)
AND (CASE WHEN IN_PO_NUMBER != "" THEN SO.PO_NUMBER = IN_PO_NUMBER
	ELSE 1 = 1 END)
) AS SALES_OUTPUT_TBL ORDER BY SO_DATE, DR_ID, DR_DATE, ARI_DATE;

END//