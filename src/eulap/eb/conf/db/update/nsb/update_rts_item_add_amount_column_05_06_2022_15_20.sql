
-- Description	: Add RTS item amount column

ALTER TABLE R_RETURN_TO_SUPPLIER_ITEM ADD COLUMN AMOUNT DOUBLE DEFAULT 0;

UPDATE R_RETURN_TO_SUPPLIER_ITEM SET AMOUNT = ROUND((QUANTITY * COALESCE(UNIT_COST, 0)-COALESCE(DISCOUNT, 0)), 2);
