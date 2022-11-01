
-- Description : Update script that will add PARENT_DIVISION_ID in DIVISION table

ALTER TABLE DIVISION ADD COLUMN PARENT_DIVISION_ID int(10) unsigned DEFAULT NULL;