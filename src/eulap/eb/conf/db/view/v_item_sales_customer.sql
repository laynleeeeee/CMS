-- Description: View for item sales by customer report. 


Delimiter //
DROP VIEW IF EXISTS V_ITEM_SALES_CUSTOMER;

CREATE VIEW V_ITEM_SALES_CUSTOMER AS


SELECT C.COMPANY_ID, AC.AR_CUSTOMER_ID, ACA.AR_CUSTOMER_ACCOUNT_ID, IC.ITEM_CATEGORY_ID, I.ITEM_ID,
RECEIPT_DATE AS DATE, STOCK_CODE, I.DESCRIPTION, CONCAT('CS ', CS_NUMBER) AS REF_NO, QUANTITY,
UM.NAME AS UOM, SRP, (QUANTITY * SRP) AS AMOUNT, COALESCE(DISCOUNT, 0) AS DISCOUNT,
(QUANTITY * SRP) - COALESCE(DISCOUNT, 0) AS NET_AMOUNT, '' AS DIVISION_NAME FROM CASH_SALE_ITEM CSI
INNER JOIN CASH_SALE CS ON CS.CASH_SALE_ID = CSI.CASH_SALE_ID
INNER JOIN ITEM I ON I.ITEM_ID = CSI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = CS.COMPANY_ID
INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = CS.AR_CUSTOMER_ID
INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON  ACA.AR_CUSTOMER_ACCOUNT_ID = CS.AR_CUSTOMER_ACCOUNT_ID
INNER JOIN ITEM_CATEGORY IC ON IC.ITEM_CATEGORY_ID = I.ITEM_CATEGORY_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CS.FORM_WORKFLOW_ID
WHERE FW.IS_COMPLETE = 1 AND CS.CASH_SALE_TYPE_ID = 1

UNION ALL 

SELECT C.COMPANY_ID, AC.AR_CUSTOMER_ID, ACA.AR_CUSTOMER_ACCOUNT_ID, IC.ITEM_CATEGORY_ID, I.ITEM_ID,
RECEIPT_DATE AS DATE, STOCK_CODE, I.DESCRIPTION, CONCAT('CS-IS ', CS_NUMBER) AS REF_NO, QUANTITY,
UM.NAME AS UOM, SRP, (QUANTITY * SRP) AS AMOUNT, COALESCE(DISCOUNT, 0) AS DISCOUNT,
(QUANTITY * SRP) - COALESCE(DISCOUNT, 0) AS NET_AMOUNT,'' AS DIVISION_NAME FROM CASH_SALE_ITEM CSI
INNER JOIN CASH_SALE CS ON CS.CASH_SALE_ID = CSI.CASH_SALE_ID
INNER JOIN ITEM I ON I.ITEM_ID = CSI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = CS.COMPANY_ID
INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = CS.AR_CUSTOMER_ID
INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON  ACA.AR_CUSTOMER_ACCOUNT_ID = CS.AR_CUSTOMER_ACCOUNT_ID
INNER JOIN ITEM_CATEGORY IC ON IC.ITEM_CATEGORY_ID = I.ITEM_CATEGORY_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CS.FORM_WORKFLOW_ID
WHERE FW.IS_COMPLETE = 1 AND CS.CASH_SALE_TYPE_ID = 3

UNION ALL

-- Cash Sale - Processing
SELECT C.COMPANY_ID, AC.AR_CUSTOMER_ID, ACA.AR_CUSTOMER_ACCOUNT_ID, IC.ITEM_CATEGORY_ID, I.ITEM_ID,
RECEIPT_DATE AS DATE, STOCK_CODE, I.DESCRIPTION, CONCAT('CS-P ', CS_NUMBER) AS REF_NO, QUANTITY,
UM.NAME AS UOM, SRP, (QUANTITY * SRP) AS AMOUNT, COALESCE(DISCOUNT, 0) AS DISCOUNT,
(QUANTITY * SRP) - COALESCE(DISCOUNT, 0) AS NET_AMOUNT, '' AS DIVISION_NAME FROM CASH_SALE_ITEM CSI
INNER JOIN CASH_SALE CS ON CS.CASH_SALE_ID = CSI.CASH_SALE_ID
INNER JOIN ITEM I ON I.ITEM_ID = CSI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = CS.COMPANY_ID
INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = CS.AR_CUSTOMER_ID
INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON  ACA.AR_CUSTOMER_ACCOUNT_ID = CS.AR_CUSTOMER_ACCOUNT_ID
INNER JOIN ITEM_CATEGORY IC ON IC.ITEM_CATEGORY_ID = I.ITEM_CATEGORY_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CS.FORM_WORKFLOW_ID
WHERE FW.IS_COMPLETE = 1 AND CS.CASH_SALE_TYPE_ID = 6

UNION ALL

SELECT C.COMPANY_ID, AC.AR_CUSTOMER_ID, ACA.AR_CUSTOMER_ACCOUNT_ID, IC.ITEM_CATEGORY_ID, I.ITEM_ID,
TRANSACTION_DATE AS DATE, STOCK_CODE, I.DESCRIPTION, CONCAT('AS ', SEQUENCE_NO)  AS REF_NO,
QUANTITY, UM.NAME AS UOM, SRP, (QUANTITY * SRP) AS AMOUNT, COALESCE(DISCOUNT, 0) AS DISCOUNT,
(QUANTITY * SRP) - COALESCE(DISCOUNT, 0) AS NET_AMOUNT, D.NAME AS DIVISION_NAME FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ART.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID
INNER JOIN DIVISION D ON ART.DIVISION_ID = D.DIVISION_ID
INNER JOIN ITEM I ON I.ITEM_ID = ASI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = ART.COMPANY_ID
INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = ART.CUSTOMER_ID
INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON  ACA.AR_CUSTOMER_ACCOUNT_ID = ART.CUSTOMER_ACCOUNT_ID
INNER JOIN ITEM_CATEGORY IC ON IC.ITEM_CATEGORY_ID = I.ITEM_CATEGORY_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ART.FORM_WORKFLOW_ID
WHERE AR_TRANSACTION_TYPE_ID = 4
AND FW.IS_COMPLETE = 1

UNION ALL

SELECT C.COMPANY_ID, AC.AR_CUSTOMER_ID, ACA.AR_CUSTOMER_ACCOUNT_ID, IC.ITEM_CATEGORY_ID, I.ITEM_ID,
TRANSACTION_DATE AS DATE, STOCK_CODE, I.DESCRIPTION, CONCAT('AS ', SEQUENCE_NO)  AS REF_NO,
QUANTITY, UM.NAME AS UOM, SRP, (QUANTITY * SRP) AS AMOUNT, COALESCE(DISCOUNT, 0) AS DISCOUNT,
(QUANTITY * SRP) - COALESCE(DISCOUNT, 0) AS NET_AMOUNT, D.NAME AS DIVISION_NAME FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ART.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID
INNER JOIN DIVISION D ON ART.DIVISION_ID = D.DIVISION_ID
INNER JOIN ITEM I ON I.ITEM_ID = ASI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = ART.COMPANY_ID
INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = ART.CUSTOMER_ID
INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON  ACA.AR_CUSTOMER_ACCOUNT_ID = ART.CUSTOMER_ACCOUNT_ID
INNER JOIN ITEM_CATEGORY IC ON IC.ITEM_CATEGORY_ID = I.ITEM_CATEGORY_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ART.FORM_WORKFLOW_ID
WHERE AR_TRANSACTION_TYPE_ID = 12
AND FW.IS_COMPLETE = 1

UNION ALL

SELECT C.COMPANY_ID, AC.AR_CUSTOMER_ID, ACA.AR_CUSTOMER_ACCOUNT_ID, IC.ITEM_CATEGORY_ID, I.ITEM_ID,
TRANSACTION_DATE AS DATE, STOCK_CODE, I.DESCRIPTION, CONCAT('AS-IS ', SEQUENCE_NO)  AS REF_NO,
QUANTITY, UM.NAME AS UOM, SRP, (QUANTITY * SRP) AS AMOUNT, COALESCE(DISCOUNT, 0) AS DISCOUNT,
(QUANTITY * SRP) - COALESCE(DISCOUNT, 0) AS NET_AMOUNT, D.NAME AS DIVISION_NAME FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ART.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID
INNER JOIN DIVISION D ON ART.DIVISION_ID = D.DIVISION_ID
INNER JOIN ITEM I ON I.ITEM_ID = ASI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = ART.COMPANY_ID
INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = ART.CUSTOMER_ID
INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON  ACA.AR_CUSTOMER_ACCOUNT_ID = ART.CUSTOMER_ACCOUNT_ID
INNER JOIN ITEM_CATEGORY IC ON IC.ITEM_CATEGORY_ID = I.ITEM_CATEGORY_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ART.FORM_WORKFLOW_ID
WHERE AR_TRANSACTION_TYPE_ID = 10
AND FW.IS_COMPLETE = 1

UNION ALL

SELECT C.COMPANY_ID, AC.AR_CUSTOMER_ID, ACA.AR_CUSTOMER_ACCOUNT_ID, IC.ITEM_CATEGORY_ID, I.ITEM_ID,
DATE, STOCK_CODE, I.DESCRIPTION, CONCAT('CSR ', CSR_NUMBER) AS REF_NO,
QUANTITY, UM.NAME AS UOM, SRP, (QUANTITY * SRP) AS AMOUNT, COALESCE(DISCOUNT, 0) AS DISCOUNT,
(QUANTITY * SRP) - COALESCE(DISCOUNT, 0) AS NET_AMOUNT, '' AS DIVISION_NAME FROM CASH_SALE_RETURN_ITEM CSRI
INNER JOIN CASH_SALE_RETURN CSR ON CSR.CASH_SALE_RETURN_ID = CSRI.CASH_SALE_RETURN_ID
INNER JOIN ITEM I ON I.ITEM_ID = CSRI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = CSR.COMPANY_ID
INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = CSR.AR_CUSTOMER_ID
INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON  ACA.AR_CUSTOMER_ACCOUNT_ID = CSR.AR_CUSTOMER_ACCOUNT_ID
INNER JOIN ITEM_CATEGORY IC ON IC.ITEM_CATEGORY_ID = I.ITEM_CATEGORY_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CSR.FORM_WORKFLOW_ID
WHERE FW.IS_COMPLETE = 1 AND CSR.CASH_SALE_TYPE_ID=1

UNION ALL

SELECT C.COMPANY_ID, AC.AR_CUSTOMER_ID, ACA.AR_CUSTOMER_ACCOUNT_ID, IC.ITEM_CATEGORY_ID, I.ITEM_ID,
DATE, STOCK_CODE, I.DESCRIPTION, CONCAT('CSR-IS ', CSR_NUMBER) AS REF_NO,
QUANTITY, UM.NAME AS UOM, SRP, (QUANTITY * SRP) AS AMOUNT, COALESCE(DISCOUNT, 0) AS DISCOUNT,
(QUANTITY * SRP) - COALESCE(DISCOUNT, 0) AS NET_AMOUNT, '' AS DIVISION_NAME FROM CASH_SALE_RETURN_ITEM CSRI
INNER JOIN CASH_SALE_RETURN CSR ON CSR.CASH_SALE_RETURN_ID = CSRI.CASH_SALE_RETURN_ID
INNER JOIN ITEM I ON I.ITEM_ID = CSRI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = CSR.COMPANY_ID
INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = CSR.AR_CUSTOMER_ID
INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON  ACA.AR_CUSTOMER_ACCOUNT_ID = CSR.AR_CUSTOMER_ACCOUNT_ID
INNER JOIN ITEM_CATEGORY IC ON IC.ITEM_CATEGORY_ID = I.ITEM_CATEGORY_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CSR.FORM_WORKFLOW_ID
WHERE FW.IS_COMPLETE = 1 AND CSR.CASH_SALE_TYPE_ID=3

UNION ALL

SELECT C.COMPANY_ID, AC.AR_CUSTOMER_ID, ACA.AR_CUSTOMER_ACCOUNT_ID, IC.ITEM_CATEGORY_ID, I.ITEM_ID,
TRANSACTION_DATE AS DATE, STOCK_CODE, I.DESCRIPTION, CONCAT('ASR ', SEQUENCE_NO)  AS REF_NO,
QUANTITY, UM.NAME AS UOM, SRP, (QUANTITY * SRP) AS AMOUNT, COALESCE(DISCOUNT, 0) AS DISCOUNT,
(QUANTITY * SRP) - COALESCE(DISCOUNT, 0) AS NET_AMOUNT, D.NAME AS DIVISION_NAME FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ART.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID
INNER JOIN DIVISION D ON ART.DIVISION_ID = D.DIVISION_ID
INNER JOIN ITEM I ON I.ITEM_ID = ASI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = ART.COMPANY_ID
INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = ART.CUSTOMER_ID
INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON  ACA.AR_CUSTOMER_ACCOUNT_ID = ART.CUSTOMER_ACCOUNT_ID
INNER JOIN ITEM_CATEGORY IC ON IC.ITEM_CATEGORY_ID = I.ITEM_CATEGORY_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ART.FORM_WORKFLOW_ID
WHERE AR_TRANSACTION_TYPE_ID = 5
AND FW.IS_COMPLETE = 1

UNION ALL

SELECT C.COMPANY_ID, AC.AR_CUSTOMER_ID, ACA.AR_CUSTOMER_ACCOUNT_ID, IC.ITEM_CATEGORY_ID, I.ITEM_ID,
TRANSACTION_DATE AS DATE, STOCK_CODE, I.DESCRIPTION, CONCAT('ASR-IS', SEQUENCE_NO)  AS REF_NO,
QUANTITY, UM.NAME AS UOM, SRP, (QUANTITY * SRP) AS AMOUNT, COALESCE(DISCOUNT, 0) AS DISCOUNT,
(QUANTITY * SRP) - COALESCE(DISCOUNT, 0) AS NET_AMOUNT, D.NAME AS DIVISION_NAME FROM ACCOUNT_SALE_ITEM ASI 
INNER JOIN AR_TRANSACTION ART ON ART.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID
INNER JOIN DIVISION D ON ART.DIVISION_ID = D.DIVISION_ID
INNER JOIN ITEM I ON I.ITEM_ID = ASI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = ART.COMPANY_ID
INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = ART.CUSTOMER_ID
INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON  ACA.AR_CUSTOMER_ACCOUNT_ID = ART.CUSTOMER_ACCOUNT_ID
INNER JOIN ITEM_CATEGORY IC ON IC.ITEM_CATEGORY_ID = I.ITEM_CATEGORY_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ART.FORM_WORKFLOW_ID
WHERE AR_TRANSACTION_TYPE_ID = 11
AND FW.IS_COMPLETE = 1

UNION ALL

SELECT C.COMPANY_ID, AC.AR_CUSTOMER_ID, ACA.AR_CUSTOMER_ACCOUNT_ID, IC.ITEM_CATEGORY_ID, I.ITEM_ID,
TRANSACTION_DATE AS DATE, STOCK_CODE, I.DESCRIPTION, CONCAT('ASR ', SEQUENCE_NO)  AS REF_NO,
QUANTITY, UM.NAME AS UOM, SRP, (QUANTITY * SRP) AS AMOUNT, COALESCE(DISCOUNT, 0) AS DISCOUNT,
(QUANTITY * SRP) - COALESCE(DISCOUNT, 0) AS NET_AMOUNT, D.NAME AS DIVISION_NAME FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ART.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID
INNER JOIN DIVISION D ON ART.DIVISION_ID = D.DIVISION_ID
INNER JOIN ITEM I ON I.ITEM_ID = ASI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = ART.COMPANY_ID
INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = ART.CUSTOMER_ID
INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON  ACA.AR_CUSTOMER_ACCOUNT_ID = ART.CUSTOMER_ACCOUNT_ID
INNER JOIN ITEM_CATEGORY IC ON IC.ITEM_CATEGORY_ID = I.ITEM_CATEGORY_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = ART.FORM_WORKFLOW_ID
WHERE AR_TRANSACTION_TYPE_ID = 13
AND FW.IS_COMPLETE = 1

UNION ALL

SELECT C.COMPANY_ID, AC.AR_CUSTOMER_ID, ACA.AR_CUSTOMER_ACCOUNT_ID, IC.ITEM_CATEGORY_ID, I.ITEM_ID,
DELIVERY_DATE AS DATE, STOCK_CODE, I.DESCRIPTION, CONCAT((CASE WHEN CAPD.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 1 THEN 'PIAD '
WHEN CAPD.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 3 THEN 'PIAD - IS '
WHEN CAPD.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 5 THEN 'PIAD - WIPSO ' END), SALES_INVOICE_NO)  AS REF_NO,
QUANTITY, UM.NAME AS UOM, SRP, (QUANTITY * SRP) AS AMOUNT, COALESCE(DISCOUNT, 0) AS DISCOUNT,
(QUANTITY * SRP) - COALESCE(DISCOUNT, 0) AS NET_AMOUNT, '' AS DIVISION_NAME FROM CAP_DELIVERY_ITEM CAPDI
INNER JOIN CAP_DELIVERY CAPD ON CAPD.CAP_DELIVERY_ID = CAPDI.CAP_DELIVERY_ID
INNER JOIN ITEM I ON I.ITEM_ID = CAPDI.ITEM_ID
INNER JOIN UNIT_MEASUREMENT UM ON UM.UNITOFMEASUREMENT_ID = I.UNIT_MEASUREMENT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = CAPD.COMPANY_ID
INNER JOIN AR_CUSTOMER AC ON AC.AR_CUSTOMER_ID = CAPD.AR_CUSTOMER_ID
INNER JOIN AR_CUSTOMER_ACCOUNT ACA ON  ACA.AR_CUSTOMER_ACCOUNT_ID = CAPD.AR_CUSTOMER_ACCOUNT_ID
INNER JOIN ITEM_CATEGORY IC ON IC.ITEM_CATEGORY_ID = I.ITEM_CATEGORY_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAPD.FORM_WORKFLOW_ID
WHERE FW.IS_COMPLETE = 1

UNION ALL

SELECT '' AS COMPANY_ID, '' AS AR_CUSTOMER_ID, '' AS AR_CUSTOMER_ACCOUNT_ID,
IC.ITEM_CATEGORY_ID, I.ITEM_ID,'' AS DATE, I.STOCK_CODE AS STOCK_CODE, I.DESCRIPTION,
'' AS REF_NO, QUANTITY, '' AS UOM, SRP, (QUANTITY * SRP ) AS AMOUNT,
COALESCE(DISCOUNT, 0) AS DISCOUNT,
(QUANTITY * SRP) - COALESCE(DISCOUNT, 0) AS NET_AMOUNT, D.NAME AS DIVISION_NAME 
FROM SERIAL_ITEM SI
INNER JOIN ITEM I ON SI.ITEM_ID = I.ITEM_ID
INNER JOIN ITEM_CATEGORY IC ON I.ITEM_CATEGORY_ID = IC.ITEM_CATEGORY_ID
INNER JOIN WAREHOUSE WH ON  SI.WAREHOUSE_ID = WH.WAREHOUSE_ID
INNER JOIN DIVISION D ON WH.DIVISION_ID = D.DIVISION_ID

//