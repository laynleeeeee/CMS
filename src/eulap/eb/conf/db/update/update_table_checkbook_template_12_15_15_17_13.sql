
-- Description: Alter table CHECKBOOK_TEMPLATE.

ALTER TABLE CHECKBOOK_TEMPLATE DROP COLUMN URI,
ADD COLUMN VIEWS_PROP_NAME VARCHAR(30) NOT NULL;