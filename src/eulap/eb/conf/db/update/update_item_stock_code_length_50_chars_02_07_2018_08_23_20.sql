
-- Description : Increase STOCK_CODE length to 50 characters of ITEM table.

ALTER TABLE ITEM MODIFY STOCK_CODE VARCHAR(50) NOT NULL;