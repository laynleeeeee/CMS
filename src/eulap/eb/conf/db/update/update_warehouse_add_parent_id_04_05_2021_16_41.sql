
-- Description	: Add PARENT_WAREHOUSE_ID in WAREHOUSE table

ALTER TABLE WAREHOUSE ADD PARENT_WAREHOUSE_ID INT(10) UNSIGNED DEFAULT NULL;