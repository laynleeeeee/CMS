
-- Description: Add UNIT_COST column in R_PURCHASE_ORDER_ITEM table.

ALTER TABLE `R_PURCHASE_ORDER_ITEM`
ADD COLUMN `UNIT_COST` DOUBLE DEFAULT NULL AFTER `QUANTITY`;
