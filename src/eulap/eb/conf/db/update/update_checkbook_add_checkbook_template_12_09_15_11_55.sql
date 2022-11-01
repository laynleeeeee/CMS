
-- Description: Add CHECKBOOK_TEMPLATE_ID in CHECKBOOK table

ALTER TABLE CHECKBOOK ADD CHECKBOOK_TEMPLATE_ID int(10) unsigned DEFAULT NULL,
ADD CONSTRAINT FK_CHECKBOOK_TEMPLATE_ID FOREIGN KEY (CHECKBOOK_TEMPLATE_ID)
REFERENCES CHECKBOOK_TEMPLATE (CHECKBOOK_TEMPLATE_ID);