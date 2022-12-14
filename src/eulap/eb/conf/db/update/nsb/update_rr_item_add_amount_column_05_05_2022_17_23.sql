
-- Description	: Add RR item amount column

ALTER TABLE R_RECEIVING_REPORT_ITEM ADD COLUMN AMOUNT DOUBLE DEFAULT 0;

UPDATE R_RECEIVING_REPORT_ITEM SET AMOUNT = ROUND((QUANTITY * COALESCE(UNIT_COST, 0)-COALESCE(DISCOUNT, 0)), 2);
