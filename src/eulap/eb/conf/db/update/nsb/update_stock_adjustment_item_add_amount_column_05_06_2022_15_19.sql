
-- Description	: Add stock adjustment item amount column

ALTER TABLE STOCK_ADJUSTMENT_ITEM ADD COLUMN AMOUNT DOUBLE DEFAULT 0;

UPDATE STOCK_ADJUSTMENT_ITEM SET AMOUNT = ROUND((QUANTITY * UNIT_COST), 2);