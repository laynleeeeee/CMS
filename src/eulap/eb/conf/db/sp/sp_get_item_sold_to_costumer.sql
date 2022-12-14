-- Description: Stored procedure that will get the item sold to customer


Delimiter //
DROP procedure IF EXISTS GET_ITEM_SOLD_TO_COSTUMER;//
CREATE procedure GET_ITEM_SOLD_TO_COSTUMER (IN IN_COMPANY_ID INT,IN IN_DIVISION_ID INT, IN IN_CUSTOMER_ID INT, IN IN_CUSTOMER_ACCOUNT_ID INT,
IN IN_ITEM_CATEGORY INT, IN IN_ITEM_ID INT, IN IN_DATE_FROM DATE, IN IN_DATE_TO DATE, IN IN_LIMIT_FROM INT, IN IN_LIMIT_TO INT)

BEGIN

SELECT DIVISION_NAME, DATE, STOCK_CODE, DESCRIPTION, BMS, REF_NO, QTY, UOM, SRP, AMOUNT, DISCOUNT, NET_AMOUNT FROM (

SELECT D.NAME AS DIVISION_NAME,AI.DATE, I.STOCK_CODE AS STOCK_CODE, I.DESCRIPTION,  COALESCE(SO.REMARKS, '') AS BMS, 
CONCAT('ARI ', AI.SEQUENCE_NO) AS REF_NO, AII.QUANTITY AS QTY, UM.NAME AS UOM, AII.SRP, (COALESCE(SRP,0) * AII.QUANTITY) AS AMOUNT,
COALESCE(DISCOUNT, 0) AS DISCOUNT, (AII.QUANTITY * AII.SRP)-(COALESCE(DISCOUNT, 0)) AS NET_AMOUNT
FROM AR_INVOICE_ITEM AII
INNER JOIN AR_INVOICE AI ON AII.AR_INVOICE_ID = AI.AR_INVOICE_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID
INNER JOIN DIVISION D ON AI.DIVISION_ID = D.DIVISION_ID
INNER JOIN ITEM I ON AII.ITEM_ID = I.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UM ON I.UNIT_MEASUREMENT_ID = UM.UNITOFMEASUREMENT_ID
INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = AI.DELIVERY_RECEIPT_ID
INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = DR.SALES_ORDER_ID
WHERE AI.COMPANY_ID = IN_COMPANY_ID
AND AI.AR_CUSTOMER_ID = IN_CUSTOMER_ID
AND IF(IN_DIVISION_ID != -1, D.DIVISION_ID = IN_DIVISION_ID, D.DIVISION_ID != IN_DIVISION_ID)
AND IF(IN_CUSTOMER_ACCOUNT_ID != -1, AI.AR_CUSTOMER_ACCOUNT_ID = IN_CUSTOMER_ACCOUNT_ID, AI.AR_CUSTOMER_ACCOUNT_ID != IN_CUSTOMER_ACCOUNT_ID)
AND IF(IN_ITEM_CATEGORY != -1, I.ITEM_CATEGORY_ID = IN_ITEM_CATEGORY, I.ITEM_CATEGORY_ID != IN_ITEM_CATEGORY)
AND IF(IN_ITEM_ID != -1, I.ITEM_ID = IN_ITEM_ID, I.ITEM_ID != IN_ITEM_ID)
AND AI.DATE BETWEEN IN_DATE_FROM AND IN_DATE_TO
AND FW.IS_COMPLETE = 1

UNION ALL

SELECT D.NAME AS DIVISION_NAME, AI.DATE, I.STOCK_CODE AS STOCK_CODE, I.DESCRIPTION, COALESCE(SO.REMARKS, '') AS BMS,
CONCAT('ARI ', AI.SEQUENCE_NO) AS REF_NO, SUM(SI.QUANTITY) AS QTY, UM.NAME AS UOM, SI.SRP, SUM(COALESCE(SI.SRP, 0)) AS AMOUNT,
SUM(COALESCE(SI.DISCOUNT, 0)) AS DISCOUNT, SUM(SI.SRP)-SUM(COALESCE(SI.DISCOUNT, 0)) AS NET_AMOUNT
FROM SERIAL_ITEM SI
INNER JOIN OBJECT_TO_OBJECT OTO ON SI.EB_OBJECT_ID = OTO.TO_OBJECT_ID
INNER JOIN AR_INVOICE AI ON OTO.FROM_OBJECT_ID = AI.EB_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AI.FORM_WORKFLOW_ID
INNER JOIN ITEM I ON SI.ITEM_ID = I.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UM ON I.UNIT_MEASUREMENT_ID = UM.UNITOFMEASUREMENT_ID
INNER JOIN DIVISION D ON AI.DIVISION_ID = D.DIVISION_ID
INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = AI.DELIVERY_RECEIPT_ID
INNER JOIN SALES_ORDER SO ON SO.SALES_ORDER_ID = DR.SALES_ORDER_ID
WHERE SI.ACTIVE = 1
AND AI.COMPANY_ID = IN_COMPANY_ID
AND AI.AR_CUSTOMER_ID = IN_CUSTOMER_ID
AND IF(IN_DIVISION_ID != -1, D.DIVISION_ID = IN_DIVISION_ID, D.DIVISION_ID != IN_DIVISION_ID)
AND IF(IN_CUSTOMER_ACCOUNT_ID != -1, AI.AR_CUSTOMER_ACCOUNT_ID = IN_CUSTOMER_ACCOUNT_ID, AI.AR_CUSTOMER_ACCOUNT_ID != IN_CUSTOMER_ACCOUNT_ID)
AND IF(IN_ITEM_CATEGORY != -1, I.ITEM_CATEGORY_ID = IN_ITEM_CATEGORY, I.ITEM_CATEGORY_ID != IN_ITEM_CATEGORY)
AND IF(IN_ITEM_ID != -1, I.ITEM_ID = IN_ITEM_ID, I.ITEM_ID != IN_ITEM_ID)
AND AI.DATE BETWEEN IN_DATE_FROM AND IN_DATE_TO
AND FW.IS_COMPLETE = 1
AND OTO.OR_TYPE_ID = 12006
GROUP BY AI.AR_INVOICE_ID, SI.ITEM_ID

) AS TBL ORDER BY DATE, REF_NO;
END //