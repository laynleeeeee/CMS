
-- Description : Update script that will add PARENT_ACCOUNT_ID in ACCOUNT table

ALTER TABLE ACCOUNT ADD COLUMN PARENT_ACCOUNT_ID int(10) unsigned DEFAULT NULL;