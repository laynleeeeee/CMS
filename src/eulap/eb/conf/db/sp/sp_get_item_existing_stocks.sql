-- Description: Stored procedure that will get the existing stocks of the retail item.


Delimiter //
DROP PROCEDURE IF EXISTS GET_ITEM_EXISTING_STOCKS;
CREATE PROCEDURE GET_ITEM_EXISTING_STOCKS(IN IN_ITEM_ID int, IN IN_WAREHOUSE_ID int, IN AS_OF_DATE date, IN IN_COMPANY_ID int)
BEGIN
SELECT SUM(QUANTITY) as EXISTING_STOCKS FROM (

SELECT API.AP_INVOICE_ID as ID, API.GL_DATE as DATE,
RRI.QUANTITY - COALESCE((SELECT sum(RTSI.QUANTITY) from R_RETURN_TO_SUPPLIER_ITEM RTSI
INNER JOIN AP_INVOICE API ON RTSI.AP_INVOICE_ID = API.AP_INVOICE_ID
INNER JOIN FORM_WORKFLOW FW ON API.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4 
AND RRI.R_RECEIVING_REPORT_ITEM_ID = RTSI.R_RECEIVING_REPORT_ITEM_ID
AND API.INVOICE_TYPE_ID IN (31,32,33,34,35,36)), 0) 
as QUANTITY,
'RR' as FORM FROM R_RECEIVING_REPORT_ITEM RRI
INNER JOIN AP_INVOICE API ON RRI.AP_INVOICE_ID = API.AP_INVOICE_ID
INNER JOIN R_RECEIVING_REPORT RR ON API.AP_INVOICE_ID = RR.AP_INVOICE_ID
INNER JOIN FORM_WORKFLOW FW ON API.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE RRI.ITEM_ID = IN_ITEM_ID AND API.GL_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN RR.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE RR.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN RR.COMPANY_ID != IN_COMPANY_ID
	ELSE RR.COMPANY_ID = IN_COMPANY_ID END)
AND FW.IS_COMPLETE = 1

UNION ALL

SELECT API.AP_INVOICE_ID as ID, API.GL_DATE as DATE,
SUM(SI.QUANTITY) - COALESCE((SELECT SUM(RTS_SI.QUANTITY) FROM SERIAL_ITEM RTS_SI
INNER JOIN OBJECT_TO_OBJECT RTS_OTO ON RTS_OTO.TO_OBJECT_ID = RTS_SI.EB_OBJECT_ID
INNER JOIN AP_INVOICE RTS_API ON RTS_API.EB_OBJECT_ID = RTS_OTO.FROM_OBJECT_ID
INNER JOIN FORM_WORKFLOW RTS_FW ON RTS_FW.FORM_WORKFLOW_ID = RTS_API.FORM_WORKFLOW_ID
WHERE RTS_FW.CURRENT_STATUS_ID != 4
AND RTS_SI.ACTIVE = 1
AND RTS_API.GL_DATE <= NOW()
AND RTS_API.INVOICE_TYPE_ID IN (31,32,33,34,35,36)
AND RTS_OTO.OR_TYPE_ID = 105
AND RTS_SI.ITEM_ID = SI.ITEM_ID),0)
as QUANTITY,
'RR' as FORM FROM SERIAL_ITEM SI 
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID
INNER JOIN AP_INVOICE API ON API.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
INNER JOIN R_RECEIVING_REPORT RR ON API.AP_INVOICE_ID = RR.AP_INVOICE_ID
INNER JOIN FORM_WORKFLOW FW ON API.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE SI.ITEM_ID = IN_ITEM_ID
AND API.GL_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN RR.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE RR.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN RR.COMPANY_ID != IN_COMPANY_ID
	ELSE RR.COMPANY_ID = IN_COMPANY_ID END)
AND SI.ACTIVE = 1
AND FW.IS_COMPLETE = 1

UNION ALL

SELECT SA.STOCK_ADJUSTMENT_ID, SA.SA_DATE, SI.QUANTITY, 'SA-IN' FROM SERIAL_ITEM SI 
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID 
INNER JOIN STOCK_ADJUSTMENT SA ON SA.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON SA.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE SI.ITEM_ID = IN_ITEM_ID AND SA.SA_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN SA.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE SA.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN SA.COMPANY_ID != IN_COMPANY_ID
	ELSE SA.COMPANY_ID = IN_COMPANY_ID END)
AND OTO.OR_TYPE_ID = 56
AND SI.ACTIVE = 1
AND FW.IS_COMPLETE = 1

UNION ALL

SELECT SA.STOCK_ADJUSTMENT_ID, SA.SA_DATE, SI.QUANTITY, 'SA-OUT' FROM SERIAL_ITEM SI 
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID 
INNER JOIN STOCK_ADJUSTMENT SA ON SA.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON SA.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE SI.ITEM_ID = IN_ITEM_ID AND SA.SA_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN SA.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE SA.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN SA.COMPANY_ID != IN_COMPANY_ID
	ELSE SA.COMPANY_ID = IN_COMPANY_ID END)
AND OTO.OR_TYPE_ID = 63
AND SI.ACTIVE = 1
AND FW.CURRENT_STATUS_ID != 4

UNION ALL

SELECT API.AP_INVOICE_ID as ID, API.GL_DATE as DATE,
-RTSI.QUANTITY,
'RTS-EB' as FORM FROM R_RETURN_TO_SUPPLIER_ITEM RTSI
INNER JOIN AP_INVOICE API ON RTSI.AP_INVOICE_ID = API.AP_INVOICE_ID
INNER JOIN R_RETURN_TO_SUPPLIER RTS ON API.AP_INVOICE_ID = RTS.AP_INVOICE_ID
INNER JOIN FORM_WORKFLOW FW ON API.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE RTSI.ITEM_ID = IN_ITEM_ID AND API.GL_DATE <= AS_OF_DATE
AND RTS.WAREHOUSE_ID = IN_WAREHOUSE_ID
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN RTS.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE RTS.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN RTS.COMPANY_ID != IN_COMPANY_ID
	ELSE RTS.COMPANY_ID = IN_COMPANY_ID END)
AND FW.CURRENT_STATUS_ID != 4
AND API.INVOICE_TYPE_ID = 7
UNION ALL

SELECT TR.R_TRANSFER_RECEIPT_ID, TR.TR_DATE, TRI.QUANTITY, 'TR To' FROM R_TRANSFER_RECEIPT_ITEM TRI
INNER JOIN R_TRANSFER_RECEIPT TR ON TRI.R_TRANSFER_RECEIPT_ID = TR.R_TRANSFER_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON TR.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE TRI.ITEM_ID = IN_ITEM_ID AND TR.TR_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN TR.WAREHOUSE_TO_ID != IN_WAREHOUSE_ID
	ELSE TR.WAREHOUSE_TO_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN TR.COMPANY_ID != IN_COMPANY_ID
	ELSE TR.COMPANY_ID = IN_COMPANY_ID END)
AND FW.IS_COMPLETE = 1
UNION ALL

SELECT RP.REPACKING_ID, RP.R_DATE, RPI.REPACKED_QUANTITY, 'RP To' FROM REPACKING_ITEM RPI
INNER JOIN REPACKING RP ON RP.REPACKING_ID = RPI.REPACKING_ID
INNER JOIN FORM_WORKFLOW FW ON RP.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE RPI.TO_ITEM_ID = IN_ITEM_ID AND RP.R_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN RP.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE RP.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN RP.COMPANY_ID != IN_COMPANY_ID
	ELSE RP.COMPANY_ID = IN_COMPANY_ID END)
AND RP.REPACKING_TYPE_ID = 1
AND FW.IS_COMPLETE = 1

UNION ALL

SELECT ART.AR_TRANSACTION_ID, ART.TRANSACTION_DATE, ASI.QUANTITY*(-1), 'ASR-Ret' FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ASI.AR_TRANSACTION_ID = ART.AR_TRANSACTION_ID
INNER JOIN FORM_WORKFLOW FW ON ART.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE ASI.ITEM_ID = IN_ITEM_ID AND ART.TRANSACTION_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN ASI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE ASI.WAREHOUSE_ID  = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN ART.COMPANY_ID != IN_COMPANY_ID
	ELSE ART.COMPANY_ID = IN_COMPANY_ID END)
AND ART.AR_TRANSACTION_TYPE_ID = 5 AND ASI.QUANTITY > 0 AND FW.CURRENT_STATUS_ID != 4
UNION ALL

SELECT ART.AR_TRANSACTION_ID, ART.TRANSACTION_DATE, ASI.QUANTITY*(-1), 'ASR-Ex' FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ASI.AR_TRANSACTION_ID = ART.AR_TRANSACTION_ID
INNER JOIN FORM_WORKFLOW FW ON ART.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE ASI.ITEM_ID = IN_ITEM_ID AND ART.TRANSACTION_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN ASI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE ASI.WAREHOUSE_ID  = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN ART.COMPANY_ID != IN_COMPANY_ID
	ELSE ART.COMPANY_ID = IN_COMPANY_ID END)
AND ART.AR_TRANSACTION_TYPE_ID = 5 AND ASI.QUANTITY < 0 AND FW.IS_COMPLETE = 1
UNION ALL

SELECT ART.AR_TRANSACTION_ID, ART.TRANSACTION_DATE, -ASI.QUANTITY, 'ASRW-Ret' FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ASI.AR_TRANSACTION_ID = ART.AR_TRANSACTION_ID
INNER JOIN FORM_WORKFLOW FW ON ART.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE ASI.ITEM_ID = IN_ITEM_ID AND ART.TRANSACTION_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN ASI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE ASI.WAREHOUSE_ID  = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN ART.COMPANY_ID != IN_COMPANY_ID
	ELSE ART.COMPANY_ID = IN_COMPANY_ID END)
AND ART.AR_TRANSACTION_TYPE_ID = 9 AND ASI.QUANTITY > 0 AND FW.CURRENT_STATUS_ID != 4
UNION ALL

SELECT ART.AR_TRANSACTION_ID, ART.TRANSACTION_DATE, -ASI.QUANTITY, 'ASRW-Ex' FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ASI.AR_TRANSACTION_ID = ART.AR_TRANSACTION_ID
INNER JOIN FORM_WORKFLOW FW ON ART.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE ASI.ITEM_ID = IN_ITEM_ID AND ART.TRANSACTION_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN ASI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE ASI.WAREHOUSE_ID  = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN ART.COMPANY_ID != IN_COMPANY_ID
	ELSE ART.COMPANY_ID = IN_COMPANY_ID END)
AND ART.AR_TRANSACTION_TYPE_ID = 9 AND ASI.QUANTITY < 0 AND FW.IS_COMPLETE = 1
UNION ALL

SELECT ART.AR_TRANSACTION_ID, ART.TRANSACTION_DATE, ASI.QUANTITY, 'ASR-EB' FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ASI.AR_TRANSACTION_ID = ART.AR_TRANSACTION_ID
INNER JOIN FORM_WORKFLOW FW ON ART.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE ASI.ITEM_ID = IN_ITEM_ID AND ART.TRANSACTION_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN ASI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE ASI.WAREHOUSE_ID  = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN ART.COMPANY_ID != IN_COMPANY_ID
	ELSE ART.COMPANY_ID = IN_COMPANY_ID END)
AND ART.AR_TRANSACTION_TYPE_ID = 7 AND FW.IS_COMPLETE = 1
UNION ALL

SELECT ART.AR_TRANSACTION_ID, ART.TRANSACTION_DATE, ASI.QUANTITY*(-1), 'ASR-Ret' FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ASI.AR_TRANSACTION_ID = ART.AR_TRANSACTION_ID
INNER JOIN FORM_WORKFLOW FW ON ART.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE ASI.ITEM_ID = IN_ITEM_ID AND ART.TRANSACTION_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN ASI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE ASI.WAREHOUSE_ID  = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN ART.COMPANY_ID != IN_COMPANY_ID
	ELSE ART.COMPANY_ID = IN_COMPANY_ID END)
AND ART.AR_TRANSACTION_TYPE_ID = 13 AND ASI.QUANTITY > 0 AND FW.CURRENT_STATUS_ID != 4
UNION ALL

SELECT ART.AR_TRANSACTION_ID, ART.TRANSACTION_DATE, ASI.QUANTITY*(-1), 'ASR-Ex' FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ASI.AR_TRANSACTION_ID = ART.AR_TRANSACTION_ID
INNER JOIN FORM_WORKFLOW FW ON ART.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE ASI.ITEM_ID = IN_ITEM_ID AND ART.TRANSACTION_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN ASI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE ASI.WAREHOUSE_ID  = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN ART.COMPANY_ID != IN_COMPANY_ID
	ELSE ART.COMPANY_ID = IN_COMPANY_ID END)
AND ART.AR_TRANSACTION_TYPE_ID = 13 AND ASI.QUANTITY < 0 AND FW.IS_COMPLETE = 1
UNION ALL

SELECT CSR.CASH_SALE_RETURN_ID, CSR.DATE, CSRI.QUANTITY*(-1), 'CSR' FROM CASH_SALE_RETURN_ITEM CSRI
INNER JOIN CASH_SALE_RETURN CSR ON CSRI.CASH_SALE_RETURN_ID = CSR.CASH_SALE_RETURN_ID
INNER JOIN FORM_WORKFLOW FW ON CSR.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE CSRI.ITEM_ID = IN_ITEM_ID AND CSR.DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN CSRI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE CSRI.WAREHOUSE_ID  = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN CSR.COMPANY_ID != IN_COMPANY_ID
	ELSE CSR.COMPANY_ID = IN_COMPANY_ID END)
AND CSRI.QUANTITY > 0 AND FW.CURRENT_STATUS_ID != 4
UNION ALL

SELECT CSR.CASH_SALE_RETURN_ID, CSR.DATE, CSRI.QUANTITY*(-1), 'CSR' FROM CASH_SALE_RETURN_ITEM CSRI
INNER JOIN CASH_SALE_RETURN CSR ON CSRI.CASH_SALE_RETURN_ID = CSR.CASH_SALE_RETURN_ID
INNER JOIN FORM_WORKFLOW FW ON CSR.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE CSRI.ITEM_ID = IN_ITEM_ID AND CSR.DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN CSRI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE CSRI.WAREHOUSE_ID  = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN CSR.COMPANY_ID != IN_COMPANY_ID
	ELSE CSR.COMPANY_ID = IN_COMPANY_ID END)
AND CSRI.QUANTITY < 0 AND FW.IS_COMPLETE = 1
UNION ALL

SELECT CS.CASH_SALE_ID, CS.RECEIPT_DATE, -CSI.QUANTITY, 'CS' FROM CASH_SALE_ITEM CSI
INNER JOIN CASH_SALE CS ON CSI.CASH_SALE_ID = CS.CASH_SALE_ID
INNER JOIN FORM_WORKFLOW FW ON CS.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE CSI.ITEM_ID = IN_ITEM_ID AND CS.RECEIPT_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN CSI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE CSI.WAREHOUSE_ID  = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN CS.COMPANY_ID != IN_COMPANY_ID
	ELSE CS.COMPANY_ID = IN_COMPANY_ID END)
AND FW.CURRENT_STATUS_ID != 4

UNION ALL

SELECT CS.CASH_SALE_ID, CS.RECEIPT_DATE, -CRM.QUANTITY, 'CS-RM' FROM CSI_RAW_MATERIAL CRM
INNER JOIN CASH_SALE CS ON CS.CASH_SALE_ID = CRM.CASH_SALE_ID
INNER JOIN FORM_WORKFLOW FW ON CS.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE CRM.ITEM_ID = IN_ITEM_ID AND CS.RECEIPT_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN CRM.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE CRM.WAREHOUSE_ID  = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN CS.COMPANY_ID != IN_COMPANY_ID
	ELSE CS.COMPANY_ID = IN_COMPANY_ID END)
AND CS.CASH_SALE_TYPE_ID = 6
AND FW.CURRENT_STATUS_ID != 4

UNION ALL

SELECT CS.CASH_SALE_ID, CS.RECEIPT_DATE, CFP.QUANTITY, 'CS-FP' FROM CSI_FINISHED_PRODUCT CFP
INNER JOIN CASH_SALE CS ON CS.CASH_SALE_ID = CFP.CASH_SALE_ID
INNER JOIN FORM_WORKFLOW FW ON CS.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE CFP.ITEM_ID = IN_ITEM_ID AND CS.RECEIPT_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN CFP.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE CFP.WAREHOUSE_ID  = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN CS.COMPANY_ID != IN_COMPANY_ID
	ELSE CS.COMPANY_ID = IN_COMPANY_ID END)
AND CS.CASH_SALE_TYPE_ID = 6
AND FW.CURRENT_STATUS_ID != 4

UNION ALL

SELECT TR.R_TRANSFER_RECEIPT_ID, TR.TR_DATE, -TRI.QUANTITY, 'TR From' FROM R_TRANSFER_RECEIPT_ITEM TRI
INNER JOIN R_TRANSFER_RECEIPT TR ON TRI.R_TRANSFER_RECEIPT_ID = TR.R_TRANSFER_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON TR.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE TRI.ITEM_ID = IN_ITEM_ID AND TR.TR_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN TR.WAREHOUSE_FROM_ID != IN_WAREHOUSE_ID
	ELSE TR.WAREHOUSE_FROM_ID = IN_WAREHOUSE_ID END) 
AND (CASE WHEN IN_COMPANY_ID = -1 THEN TR.COMPANY_ID != IN_COMPANY_ID
	ELSE TR.COMPANY_ID = IN_COMPANY_ID END)
AND FW.CURRENT_STATUS_ID != 4
UNION ALL

SELECT ART.AR_TRANSACTION_ID, ART.TRANSACTION_DATE, -ASI.QUANTITY, 'AS' FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ASI.AR_TRANSACTION_ID = ART.AR_TRANSACTION_ID
INNER JOIN FORM_WORKFLOW FW ON ART.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE ASI.ITEM_ID = IN_ITEM_ID AND ART.TRANSACTION_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN ASI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE ASI.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN ART.COMPANY_ID != IN_COMPANY_ID
	ELSE ART.COMPANY_ID = IN_COMPANY_ID END)
AND ART.AR_TRANSACTION_TYPE_ID = 4 AND FW.CURRENT_STATUS_ID != 4
UNION ALL

SELECT ART.AR_TRANSACTION_ID, ART.TRANSACTION_DATE, -ASI.QUANTITY, 'AS' FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ASI.AR_TRANSACTION_ID = ART.AR_TRANSACTION_ID
INNER JOIN FORM_WORKFLOW FW ON ART.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE ASI.ITEM_ID = IN_ITEM_ID AND ART.TRANSACTION_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN ASI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE ASI.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN ART.COMPANY_ID != IN_COMPANY_ID
	ELSE ART.COMPANY_ID = IN_COMPANY_ID END)
AND ART.AR_TRANSACTION_TYPE_ID = 12 AND FW.CURRENT_STATUS_ID != 4
UNION ALL

SELECT ART.AR_TRANSACTION_ID, ART.TRANSACTION_DATE, -ASI.QUANTITY, 'ASW' FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ASI.AR_TRANSACTION_ID = ART.AR_TRANSACTION_ID
INNER JOIN FORM_WORKFLOW FW ON ART.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE ASI.ITEM_ID = IN_ITEM_ID AND ART.TRANSACTION_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN ASI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE ASI.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN ART.COMPANY_ID != IN_COMPANY_ID
	ELSE ART.COMPANY_ID = IN_COMPANY_ID END)
AND ART.AR_TRANSACTION_TYPE_ID = 8 AND FW.CURRENT_STATUS_ID != 4
UNION ALL

SELECT ART.AR_TRANSACTION_ID, ART.TRANSACTION_DATE, -ASI.QUANTITY, 'AS' FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION ART ON ASI.AR_TRANSACTION_ID = ART.AR_TRANSACTION_ID
INNER JOIN FORM_WORKFLOW FW ON ART.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE ASI.ITEM_ID = IN_ITEM_ID AND ART.TRANSACTION_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN ASI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE ASI.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN ART.COMPANY_ID != IN_COMPANY_ID
	ELSE ART.COMPANY_ID = IN_COMPANY_ID END)
AND ART.AR_TRANSACTION_TYPE_ID = 14 AND FW.CURRENT_STATUS_ID != 4

UNION ALL

SELECT RP.REPACKING_ID, RP.R_DATE, -RPI.QUANTITY, 'RP From' FROM REPACKING_ITEM RPI
INNER JOIN REPACKING RP ON RP.REPACKING_ID = RPI.REPACKING_ID
INNER JOIN FORM_WORKFLOW FW ON RP.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE RPI.FROM_ITEM_ID = IN_ITEM_ID AND RP.R_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN RP.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE RP.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN RP.COMPANY_ID != IN_COMPANY_ID
	ELSE RP.COMPANY_ID = IN_COMPANY_ID END)
AND RP.REPACKING_TYPE_ID = 1
AND FW.CURRENT_STATUS_ID != 4

UNION ALL

SELECT SA.STOCK_ADJUSTMENT_ID, SA.SA_DATE, SAI.QUANTITY, 'SA' FROM STOCK_ADJUSTMENT_ITEM SAI
INNER JOIN STOCK_ADJUSTMENT SA ON SAI.STOCK_ADJUSTMENT_ID = SA.STOCK_ADJUSTMENT_ID
INNER JOIN FORM_WORKFLOW FW ON SA.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE SAI.ITEM_ID = IN_ITEM_ID AND SA.SA_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN SA.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE SA.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN SA.COMPANY_ID != IN_COMPANY_ID
	ELSE SA.COMPANY_ID = IN_COMPANY_ID END)
AND SAI.QUANTITY > 0
AND FW.IS_COMPLETE = 1
UNION ALL

SELECT SA.STOCK_ADJUSTMENT_ID, SA.SA_DATE, SAI.QUANTITY, 'SA' FROM STOCK_ADJUSTMENT_ITEM SAI
INNER JOIN STOCK_ADJUSTMENT SA ON SAI.STOCK_ADJUSTMENT_ID = SA.STOCK_ADJUSTMENT_ID
INNER JOIN FORM_WORKFLOW FW ON SA.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE SAI.ITEM_ID = IN_ITEM_ID AND SA.SA_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN SA.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE SA.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN SA.COMPANY_ID != IN_COMPANY_ID
	ELSE SA.COMPANY_ID = IN_COMPANY_ID END)
AND SAI.QUANTITY < 0
AND FW.CURRENT_STATUS_ID != 4
UNION ALL

SELECT CD.CAP_DELIVERY_ID, CD.DELIVERY_DATE, -CDI.QUANTITY, 'CAPD' FROM CAP_DELIVERY_ITEM CDI
INNER JOIN CAP_DELIVERY CD ON CDI.CAP_DELIVERY_ID = CD.CAP_DELIVERY_ID
INNER JOIN FORM_WORKFLOW FW ON CD.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE CDI.ITEM_ID = IN_ITEM_ID AND CD.DELIVERY_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN CDI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE CDI.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN CD.COMPANY_ID != IN_COMPANY_ID
	ELSE CD.COMPANY_ID = IN_COMPANY_ID END)
AND FW.CURRENT_STATUS_ID != 4 AND CD.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 1

UNION ALL

SELECT CD.CAP_DELIVERY_ID, CD.DELIVERY_DATE, -CDI.QUANTITY, 'CAPD-AS' FROM CAP_DELIVERY_ITEM CDI
INNER JOIN CAP_DELIVERY CD ON CDI.CAP_DELIVERY_ID = CD.CAP_DELIVERY_ID
INNER JOIN FORM_WORKFLOW FW ON CD.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE CDI.ITEM_ID = IN_ITEM_ID AND CD.DELIVERY_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN CDI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE CDI.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN CD.COMPANY_ID != IN_COMPANY_ID
	ELSE CD.COMPANY_ID = IN_COMPANY_ID END)
AND FW.CURRENT_STATUS_ID != 4 AND CD.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 4

UNION ALL

SELECT WS.WITHDRAWAL_SLIP_ID, WS.DATE, -WSI.QUANTITY, 'WS' FROM WITHDRAWAL_SLIP_ITEM WSI
INNER JOIN OBJECT_TO_OBJECT WSOTO ON WSOTO.TO_OBJECT_ID = WSI.EB_OBJECT_ID
INNER JOIN WITHDRAWAL_SLIP WS ON WS.EB_OBJECT_ID = WSOTO.FROM_OBJECT_ID
INNER JOIN OBJECT_TO_OBJECT WOTO ON (WOTO.TO_OBJECT_ID = WS.EB_OBJECT_ID
	AND WOTO.OR_TYPE_ID = 45)
INNER JOIN WAREHOUSE W ON W.EB_OBJECT_ID = WOTO.FROM_OBJECT_ID
INNER JOIN OBJECT_TO_OBJECT COTO ON COTO.TO_OBJECT_ID = WS.EB_OBJECT_ID
INNER JOIN COMPANY C ON C.EB_OBJECT_ID = COTO.FROM_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON WS.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE WSI.ITEM_ID = IN_ITEM_ID AND WS.DATE <= AS_OF_DATE
AND WSI.ACTIVE = 1
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN W.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE W.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN C.COMPANY_ID != IN_COMPANY_ID
	ELSE C.COMPANY_ID = IN_COMPANY_ID END)
AND FW.CURRENT_STATUS_ID != 4

UNION ALL

SELECT RP.REPACKING_ID, RP.R_DATE, -RPI.QUANTITY, 'IC - RM' FROM REPACKING_RAW_MATERIAL RPI
INNER JOIN REPACKING RP ON RP.REPACKING_ID = RPI.REPACKING_ID
INNER JOIN FORM_WORKFLOW FW ON RP.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE RPI.ITEM_ID = IN_ITEM_ID
AND RP.R_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN RP.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE RP.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN RP.COMPANY_ID != IN_COMPANY_ID
	ELSE RP.COMPANY_ID = IN_COMPANY_ID END)
AND RP.REPACKING_TYPE_ID = 2
AND FW.CURRENT_STATUS_ID != 4

UNION ALL

SELECT RP.REPACKING_ID, RP.R_DATE, RPI.QUANTITY, 'IC - FG' FROM REPACKING_FINISHED_GOOD RPI
INNER JOIN REPACKING RP ON RP.REPACKING_ID = RPI.REPACKING_ID
INNER JOIN FORM_WORKFLOW FW ON RP.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE RPI.ITEM_ID = IN_ITEM_ID
AND RP.R_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN RP.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE RP.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN RP.COMPANY_ID != IN_COMPANY_ID
	ELSE RP.COMPANY_ID = IN_COMPANY_ID END)
AND RP.REPACKING_TYPE_ID = 2
AND FW.IS_COMPLETE = 1

UNION ALL

SELECT PR.PROCESSING_REPORT_ID, PR.DATE, MPI.QUANTITY, 'PR-MP' FROM PR_MAIN_PRODUCT MPI
INNER JOIN PROCESSING_REPORT PR ON PR.PROCESSING_REPORT_ID = MPI.PROCESSING_REPORT_ID
INNER JOIN FORM_WORKFLOW FW ON PR.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE MPI.ITEM_ID = IN_ITEM_ID
AND PR.DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN MPI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE MPI.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN PR.COMPANY_ID != IN_COMPANY_ID
	ELSE PR.COMPANY_ID = IN_COMPANY_ID END)
AND PR.PROCESSING_REPORT_TYPE_ID = 6
AND FW.IS_COMPLETE = 1
	
UNION ALL

SELECT PR.PROCESSING_REPORT_ID, PR.DATE, -MPI.QUANTITY, 'PR-RM' FROM PR_RAW_MATERIALS_ITEM MPI
INNER JOIN PROCESSING_REPORT PR ON PR.PROCESSING_REPORT_ID = MPI.PROCESSING_REPORT_ID
INNER JOIN FORM_WORKFLOW FW ON PR.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE MPI.ITEM_ID = IN_ITEM_ID
AND PR.DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN MPI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE MPI.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN PR.COMPANY_ID != IN_COMPANY_ID
	ELSE PR.COMPANY_ID = IN_COMPANY_ID END)
AND PR.PROCESSING_REPORT_TYPE_ID = 6
AND FW.CURRENT_STATUS_ID != 4

UNION ALL

SELECT PR.PROCESSING_REPORT_ID, PR.DATE, MPI.QUANTITY, 'PRB-MP' FROM PR_MAIN_PRODUCT MPI
INNER JOIN PROCESSING_REPORT PR ON PR.PROCESSING_REPORT_ID = MPI.PROCESSING_REPORT_ID
INNER JOIN FORM_WORKFLOW FW ON PR.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE MPI.ITEM_ID = IN_ITEM_ID
AND PR.DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN MPI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE MPI.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN PR.COMPANY_ID != IN_COMPANY_ID
	ELSE PR.COMPANY_ID = IN_COMPANY_ID END)
AND PR.PROCESSING_REPORT_TYPE_ID = 5
AND FW.IS_COMPLETE = 1
	
UNION ALL

SELECT PR.PROCESSING_REPORT_ID, PR.DATE, -MPI.QUANTITY, 'PRB-RM' FROM PR_RAW_MATERIALS_ITEM MPI
INNER JOIN PROCESSING_REPORT PR ON PR.PROCESSING_REPORT_ID = MPI.PROCESSING_REPORT_ID
INNER JOIN FORM_WORKFLOW FW ON PR.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE MPI.ITEM_ID = IN_ITEM_ID
AND PR.DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN MPI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE MPI.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN PR.COMPANY_ID != IN_COMPANY_ID
	ELSE PR.COMPANY_ID = IN_COMPANY_ID END)
AND PR.PROCESSING_REPORT_TYPE_ID = 5
AND FW.CURRENT_STATUS_ID != 4

UNION ALL

SELECT ACCT_SALE.ACCOUNT_SALE_ID, ACCT_SALE.PO_DATE, -ASPOI.QUANTITY, 'ASPO' FROM ACCOUNT_SALES_PO_ITEM ASPOI
INNER JOIN ACCOUNT_SALE ACCT_SALE ON ASPOI.ACCOUNT_SALE_ID = ACCT_SALE.ACCOUNT_SALE_ID
INNER JOIN FORM_WORKFLOW FW ON ACCT_SALE.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
AND FW.CURRENT_STATUS_ID != 4
LEFT JOIN (
SELECT ACCT_SALE.ACCOUNT_SALE_ID FROM AR_TRANSACTION ART 
INNER JOIN FORM_WORKFLOW FW1 ON ART.FORM_WORKFLOW_ID = FW1.FORM_WORKFLOW_ID
AND FW1.CURRENT_STATUS_ID != 4
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ART.EB_OBJECT_ID AND OTO.OR_TYPE_ID = 102  
INNER JOIN ACCOUNT_SALE ACCT_SALE ON ACCT_SALE.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
INNER JOIN ACCOUNT_SALES_PO_ITEM ASPOI ON ASPOI.ACCOUNT_SALE_ID = ACCT_SALE.ACCOUNT_SALE_ID
INNER JOIN FORM_WORKFLOW FW ON ACCT_SALE.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
AND FW.CURRENT_STATUS_ID != 4
) TBL ON ACCT_SALE.ACCOUNT_SALE_ID = TBL.ACCOUNT_SALE_ID
WHERE TBL.ACCOUNT_SALE_ID IS NULL
AND ASPOI.ITEM_ID = IN_ITEM_ID AND ACCT_SALE.PO_DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN ASPOI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE ASPOI.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN ACCT_SALE.COMPANY_ID != IN_COMPANY_ID
	ELSE ACCT_SALE.COMPANY_ID = IN_COMPANY_ID END)

UNION ALL

SELECT DR.DELIVERY_RECEIPT_ID, DR.DATE, -DRI.QUANTITY, 'DR' 
FROM DELIVERY_RECEIPT_ITEM DRI
INNER JOIN DELIVERY_RECEIPT DR ON DR.DELIVERY_RECEIPT_ID = DRI.DELIVERY_RECEIPT_ID
INNER JOIN FORM_WORKFLOW FW ON DR.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE DRI.ITEM_ID = IN_ITEM_ID
AND DR.DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN DRI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE DRI.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN DR.COMPANY_ID != IN_COMPANY_ID
	ELSE DR.COMPANY_ID = IN_COMPANY_ID END)
AND DR.DELIVERY_RECEIPT_TYPE_ID IN (5,6,7,8,9,10)
AND FW.CURRENT_STATUS_ID != 4

UNION ALL

SELECT DR.DELIVERY_RECEIPT_ID, DR.DATE, -SI.QUANTITY, 'DR' 
FROM SERIAL_ITEM SI
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SI.EB_OBJECT_ID
INNER JOIN DELIVERY_RECEIPT DR ON DR.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON DR.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE SI.ITEM_ID = IN_ITEM_ID
AND SI.ACTIVE = 1
AND OTO.OR_TYPE_ID = 12004
AND DR.DATE <= AS_OF_DATE
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN SI.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE SI.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN DR.COMPANY_ID != IN_COMPANY_ID
	ELSE DR.COMPANY_ID = IN_COMPANY_ID END)
AND DR.DELIVERY_RECEIPT_TYPE_ID IN (5,6,7,8,9,10)
AND FW.CURRENT_STATUS_ID != 4

UNION ALL

SELECT API.AP_INVOICE_ID as ID, API.GL_DATE as DATE, -RTSI.QUANTITY,
'RTS-NSB' as FORM FROM R_RETURN_TO_SUPPLIER_ITEM RTSI
INNER JOIN AP_INVOICE API ON RTSI.AP_INVOICE_ID = API.AP_INVOICE_ID
INNER JOIN R_RETURN_TO_SUPPLIER RTS ON API.AP_INVOICE_ID = RTS.AP_INVOICE_ID
INNER JOIN FORM_WORKFLOW FW ON API.FORM_WORKFLOW_ID = FW.FORM_WORKFLOW_ID
WHERE RTSI.ITEM_ID = IN_ITEM_ID AND API.GL_DATE <= AS_OF_DATE
AND RTS.WAREHOUSE_ID = IN_WAREHOUSE_ID
AND (CASE WHEN IN_WAREHOUSE_ID = -1 THEN RTS.WAREHOUSE_ID != IN_WAREHOUSE_ID
	ELSE RTS.WAREHOUSE_ID = IN_WAREHOUSE_ID END)
AND (CASE WHEN IN_COMPANY_ID = -1 THEN RTS.COMPANY_ID != IN_COMPANY_ID
	ELSE RTS.COMPANY_ID = IN_COMPANY_ID END)
AND FW.CURRENT_STATUS_ID != 4
AND API.INVOICE_TYPE_ID BETWEEN 31 AND 36
) as EXISTING_STOCKS;
END //