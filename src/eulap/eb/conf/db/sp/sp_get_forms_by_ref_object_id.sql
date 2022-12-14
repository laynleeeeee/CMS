-- Description: Stored procedure that will get forms by reference object id


Delimiter //
DROP PROCEDURE IF EXISTS GET_FORMS_BY_REF_OBJECT_ID;
CREATE PROCEDURE GET_FORMS_BY_REF_OBJECT_ID(IN IN_REF_OBJECT_ID INT)
BEGIN

SELECT SOURCE, EB_OBJECT_ID FROM (
SELECT CONCAT('AS-IS ', COALESCE(CONCAT(C.COMPANY_CODE,' ',AT.SEQUENCE_NO), AT.SEQUENCE_NO)) AS SOURCE, ASI.EB_OBJECT_ID
FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION AT ON AT.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = AT.COMPANY_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ASI.EB_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AT.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND AT.AR_TRANSACTION_TYPE_ID = 10 
AND OTO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
UNION ALL
SELECT CONCAT('ASR-IS ', COALESCE(CONCAT(C.COMPANY_CODE,' ',AT.SEQUENCE_NO), AT.SEQUENCE_NO)) AS SOURCE, ASI.EB_OBJECT_ID
FROM ACCOUNT_SALE_ITEM ASI
INNER JOIN AR_TRANSACTION AT ON AT.AR_TRANSACTION_ID = ASI.AR_TRANSACTION_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = AT.COMPANY_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ASI.EB_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = AT.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND AT.AR_TRANSACTION_TYPE_ID = 11
AND OTO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
UNION ALL
SELECT CONCAT('CS-IS ', COALESCE(CONCAT(C.COMPANY_CODE,' ',CS.CS_NUMBER), CS.CS_NUMBER)) AS SOURCE, CSI.EB_OBJECT_ID
FROM CASH_SALE_ITEM CSI
INNER JOIN CASH_SALE CS ON CS.CASH_SALE_ID = CSI.CASH_SALE_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = CS.COMPANY_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = CSI.EB_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CS.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND CS.CASH_SALE_TYPE_ID = 3 
AND OTO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
UNION ALL
SELECT CONCAT('CSR-IS ', COALESCE(CONCAT(C.COMPANY_CODE,' ',CSR.CSR_NUMBER), CSR.CSR_NUMBER)) AS SOURCE, CSRI.EB_OBJECT_ID
FROM CASH_SALE_RETURN_ITEM CSRI
INNER JOIN CASH_SALE_RETURN CSR ON CSR.CASH_SALE_RETURN_ID = CSRI.CASH_SALE_RETURN_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = CSR.COMPANY_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = CSRI.EB_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CSR.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND CSR.CASH_SALE_TYPE_ID = 3 
AND OTO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
UNION ALL 
SELECT CONCAT('CAP-IS ', COALESCE(CONCAT(C.COMPANY_CODE,' ',CAP.CAP_NUMBER), CAP.CAP_NUMBER)) AS SOURCE, CAPI.EB_OBJECT_ID
FROM CUSTOMER_ADVANCE_PAYMENT_ITEM CAPI
INNER JOIN CUSTOMER_ADVANCE_PAYMENT CAP ON CAP.CUSTOMER_ADVANCE_PAYMENT_ID = CAPI.CUSTOMER_ADVANCE_PAYMENT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = CAP.COMPANY_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = CAPI.EB_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAP.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND CAP.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 3 
AND OTO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
UNION ALL 
SELECT CONCAT('PIAD-IS ', COALESCE(CONCAT(C.COMPANY_CODE,' ',CAPD.CAPD_NUMBER), CAPD.CAPD_NUMBER)) AS SOURCE, CAPDI.EB_OBJECT_ID
FROM CAP_DELIVERY_ITEM CAPDI
INNER JOIN CAP_DELIVERY CAPD ON CAPD.CAP_DELIVERY_ID = CAPDI.CAP_DELIVERY_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = CAPD.COMPANY_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = CAPDI.EB_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = CAPD.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND CAPD.CUSTOMER_ADVANCE_PAYMENT_TYPE_ID = 3 
AND OTO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
UNION ALL
SELECT CONCAT((CASE WHEN PR.PROCESSING_REPORT_TYPE_ID = 1 THEN "PR-MR "
    WHEN PR.PROCESSING_REPORT_TYPE_ID = 2 THEN "PR-MO "
    WHEN PR.PROCESSING_REPORT_TYPE_ID = 3 THEN "PR-PI "
    WHEN PR.PROCESSING_REPORT_TYPE_ID = 4 THEN "PR-PO "
    WHEN PR.PROCESSING_REPORT_TYPE_ID = 5 THEN "WIP-B "
    WHEN PR.PROCESSING_REPORT_TYPE_ID = 6 THEN "PR " END), COALESCE(CONCAT(C.COMPANY_CODE,' ',PR.SEQUENCE_NO), PR.SEQUENCE_NO)) AS SOURCE, RMI.EB_OBJECT_ID
FROM PR_RAW_MATERIALS_ITEM RMI
INNER JOIN PROCESSING_REPORT PR ON PR.PROCESSING_REPORT_ID = RMI.PROCESSING_REPORT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = PR.COMPANY_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = RMI.EB_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4
AND OTO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
UNION ALL 
SELECT CONCAT('PR-MR ', COALESCE(CONCAT(C.COMPANY_CODE,' ',PR.SEQUENCE_NO), PR.SEQUENCE_NO)) AS SOURCE, OMI.EB_OBJECT_ID
FROM PR_OTHER_MATERIALS_ITEM OMI
INNER JOIN PROCESSING_REPORT PR ON PR.PROCESSING_REPORT_ID = OMI.PROCESSING_REPORT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = PR.COMPANY_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = OMI.EB_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND PR.PROCESSING_REPORT_TYPE_ID = 1
AND OTO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
UNION ALL
SELECT CONCAT('PR-MO ', COALESCE(CONCAT(C.COMPANY_CODE,' ',PR.SEQUENCE_NO), PR.SEQUENCE_NO)) AS SOURCE, OMI.EB_OBJECT_ID
FROM PR_OTHER_MATERIALS_ITEM OMI
INNER JOIN PROCESSING_REPORT PR ON PR.PROCESSING_REPORT_ID = OMI.PROCESSING_REPORT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = PR.COMPANY_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = OMI.EB_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND PR.PROCESSING_REPORT_TYPE_ID = 2
AND OTO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
UNION ALL
SELECT CONCAT('PR-PI ', COALESCE(CONCAT(C.COMPANY_CODE,' ',PR.SEQUENCE_NO), PR.SEQUENCE_NO)) AS SOURCE, OMI.EB_OBJECT_ID
FROM PR_OTHER_MATERIALS_ITEM OMI
INNER JOIN PROCESSING_REPORT PR ON PR.PROCESSING_REPORT_ID = OMI.PROCESSING_REPORT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = PR.COMPANY_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = OMI.EB_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND PR.PROCESSING_REPORT_TYPE_ID = 3
AND OTO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
UNION ALL
SELECT CONCAT('PR-PO ', COALESCE(CONCAT(C.COMPANY_CODE,' ',PR.SEQUENCE_NO), PR.SEQUENCE_NO)) AS SOURCE, OMI.EB_OBJECT_ID
FROM PR_OTHER_MATERIALS_ITEM OMI
INNER JOIN PROCESSING_REPORT PR ON PR.PROCESSING_REPORT_ID = OMI.PROCESSING_REPORT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = PR.COMPANY_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = OMI.EB_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = PR.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND PR.PROCESSING_REPORT_TYPE_ID = 4
AND OTO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
UNION ALL
SELECT CONCAT('SAO-IS ', COALESCE(CONCAT(C.COMPANY_CODE,' ',SA.SA_NUMBER), SA.SA_NUMBER)) AS SOURCE, SAI.EB_OBJECT_ID
FROM STOCK_ADJUSTMENT_ITEM SAI
INNER JOIN STOCK_ADJUSTMENT SA ON SA.STOCK_ADJUSTMENT_ID = SAI.STOCK_ADJUSTMENT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = SA.COMPANY_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = SAI.EB_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SA.FORM_WORKFLOW_ID
WHERE SAI.QUANTITY < 0 AND FW.CURRENT_STATUS_ID != 4
AND OTO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
UNION ALL 
SELECT CONCAT('TR-IS ', COALESCE(CONCAT(C.COMPANY_CODE,' ',TR.TR_NUMBER), TR.TR_NUMBER)) AS SOURCE, TRI.EB_OBJECT_ID
FROM R_TRANSFER_RECEIPT_ITEM TRI
INNER JOIN R_TRANSFER_RECEIPT TR ON TR.R_TRANSFER_RECEIPT_ID = TRI.R_TRANSFER_RECEIPT_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = TR.COMPANY_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = TRI.EB_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = TR.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4 AND TR.TRANSFER_RECEIPT_TYPE_ID = 2
AND OTO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
UNION ALL
SELECT CONCAT('IC ', COALESCE(CONCAT(C.COMPANY_CODE, ' ', IC.ITEM_CONVERSION_NUMBER),
IC.ITEM_CONVERSION_NUMBER)) AS SOURCE, ICL.EB_OBJECT_ID
FROM ITEM_CONVERSION_LINE ICL
INNER JOIN ITEM_CONVERSION IC ON IC.ITEM_CONVERSION_ID = ICL.ITEM_CONVERSION_ID
INNER JOIN COMPANY C ON C.COMPANY_ID = IC.COMPANY_ID
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = ICL.EB_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = IC.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4
AND OTO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
) AS TBL ORDER BY SOURCE;
END //