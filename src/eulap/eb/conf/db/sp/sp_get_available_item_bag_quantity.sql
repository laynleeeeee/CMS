
-- Description: Stored procedure that will get the available/remaining item bag quantity

Delimiter //
DROP PROCEDURE IF EXISTS GET_AVAILABLE_ITEM_BAG_QTY;

CREATE PROCEDURE GET_AVAILABLE_ITEM_BAG_QTY(IN IN_REF_OBJECT_ID INT)

BEGIN
SELECT SOURCE, SOURCE_OBJECT_ID, QUANTITY FROM (

SELECT CONCAT('SAI-IS ', SA.SA_NUMBER) AS SOURCE, OTO.FROM_OBJECT_ID AS SOURCE_OBJECT_ID, IBQ.QUANTITY
FROM ITEM_BAG_QUANTITY IBQ
INNER JOIN OBJECT_TO_OBJECT OTO ON OTO.TO_OBJECT_ID = IBQ.EB_OBJECT_ID
INNER JOIN STOCK_ADJUSTMENT_ITEM SAI ON SAI.EB_OBJECT_ID = OTO.FROM_OBJECT_ID
INNER JOIN STOCK_ADJUSTMENT SA ON SA.STOCK_ADJUSTMENT_ID = SAI.STOCK_ADJUSTMENT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SA.FORM_WORKFLOW_ID
WHERE FW.IS_COMPLETE = 1
AND OTO.OR_TYPE_ID = 84
AND IBQ.ACTIVE = 1
AND SA.STOCK_ADJUSTMENT_CLASSIFICATION_ID = 3
AND OTO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
UNION ALL
SELECT CONCAT('SAO-IS ', SA.SA_NUMBER) AS SOURCE, SOURC_OO.FROM_OBJECT_ID AS SOURCE_OBJECT_ID, IBQ.QUANTITY
FROM ITEM_BAG_QUANTITY IBQ
INNER JOIN OBJECT_TO_OBJECT IBQ_OO ON IBQ_OO.TO_OBJECT_ID = IBQ.EB_OBJECT_ID
INNER JOIN STOCK_ADJUSTMENT_ITEM SAI ON SAI.EB_OBJECT_ID = IBQ_OO.FROM_OBJECT_ID
INNER JOIN STOCK_ADJUSTMENT SA ON SA.STOCK_ADJUSTMENT_ID = SAI.STOCK_ADJUSTMENT_ID
INNER JOIN OBJECT_TO_OBJECT SOURC_OO ON SOURC_OO.TO_OBJECT_ID = IBQ_OO.FROM_OBJECT_ID
INNER JOIN FORM_WORKFLOW FW ON FW.FORM_WORKFLOW_ID = SA.FORM_WORKFLOW_ID
WHERE FW.CURRENT_STATUS_ID != 4
AND IBQ_OO.OR_TYPE_ID = 92
AND SOURC_OO.OR_TYPE_ID = 3
AND IBQ.ACTIVE = 1
AND SA.STOCK_ADJUSTMENT_CLASSIFICATION_ID = 4
AND SOURC_OO.FROM_OBJECT_ID = IN_REF_OBJECT_ID
) AS AVAILABLE_BAGS
GROUP BY SOURCE_OBJECT_ID;

END
//