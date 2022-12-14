
-- Description: update the unit cost of the saved RR-raw materials for bug 1787

UPDATE R_RECEIVING_REPORT_ITEM RR
INNER JOIN R_RECEIVING_REPORT_RM_ITEM RM ON RM.R_RECEIVING_REPORT_ITEM_ID = RR.R_RECEIVING_REPORT_ITEM_ID
SET RR.UNIT_COST = (RM.AMOUNT/RR.QUANTITY)
WHERE RM.DISCOUNT IS NOT NULL OR RM.ADD_ON IS NOT NULL;