
-- Description	: Consolidated update script that will set and update warehouse with division id tagged and update their respective forms

INSERT INTO WAREHOUSE (COMPANY_ID, NAME, ADDRESS, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, PARENT_WAREHOUSE_ID, DIVISION_ID)
SELECT 1, W.NAME, W.ADDRESS, W.ACTIVE, W.CREATED_BY, W.CREATED_DATE, W.UPDATED_BY, W.UPDATED_DATE, NULL, D.DIVISION_ID
FROM WAREHOUSE W
CROSS JOIN DIVISION D
WHERE W.DIVISION_ID IS NULL
AND D.DIVISION_ID IN (2, 3, 4, 5, 6);

UPDATE WAREHOUSE SET DIVISION_ID = 1 WHERE DIVISION_ID IS NULL;

UPDATE STOCK_ADJUSTMENT SA
INNER JOIN WAREHOUSE W ON W.WAREHOUSE_ID = SA.WAREHOUSE_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SA.FORM_WORKFLOW_ID
SET SA.WAREHOUSE_ID = (SELECT W1.WAREHOUSE_ID FROM WAREHOUSE W1 WHERE W1.DIVISION_ID = SA.DIVISION_ID AND W1.NAME = W.NAME)
WHERE FW.CURRENT_STATUS_ID != 4;

UPDATE REPACKING SA
INNER JOIN WAREHOUSE W ON W.WAREHOUSE_ID = SA.WAREHOUSE_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SA.FORM_WORKFLOW_ID
SET SA.WAREHOUSE_ID = (SELECT W1.WAREHOUSE_ID FROM WAREHOUSE W1 WHERE W1.DIVISION_ID = SA.DIVISION_ID AND W1.NAME = W.NAME)
WHERE FW.CURRENT_STATUS_ID != 4;

UPDATE R_RECEIVING_REPORT SA
INNER JOIN WAREHOUSE W ON W.WAREHOUSE_ID = SA.WAREHOUSE_ID
INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = SA.AP_INVOICE_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID
SET SA.WAREHOUSE_ID = (SELECT W1.WAREHOUSE_ID FROM WAREHOUSE W1 WHERE W1.DIVISION_ID = API.DIVISION_ID AND W1.NAME = W.NAME)
WHERE FW.CURRENT_STATUS_ID != 4;

UPDATE DELIVERY_RECEIPT_ITEM SA
INNER JOIN WAREHOUSE W ON W.WAREHOUSE_ID = SA.WAREHOUSE_ID
INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = SA.DELIVERY_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = DR.FORM_WORKFLOW_ID
SET SA.WAREHOUSE_ID = (SELECT W1.WAREHOUSE_ID FROM WAREHOUSE W1 WHERE W1.DIVISION_ID = DR.DIVISION_ID AND W1.NAME = W.NAME)
WHERE FW.CURRENT_STATUS_ID != 4;

UPDATE R_RETURN_TO_SUPPLIER SA
INNER JOIN WAREHOUSE W ON W.WAREHOUSE_ID = SA.WAREHOUSE_ID
INNER JOIN AP_INVOICE API ON API.AP_INVOICE_ID = SA.AP_INVOICE_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = API.FORM_WORKFLOW_ID
SET SA.WAREHOUSE_ID = (SELECT W1.WAREHOUSE_ID FROM WAREHOUSE W1 WHERE W1.DIVISION_ID = API.DIVISION_ID AND W1.NAME = W.NAME)
WHERE FW.CURRENT_STATUS_ID != 4;