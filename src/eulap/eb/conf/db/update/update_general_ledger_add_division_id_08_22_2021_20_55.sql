
-- Description	: Add DIVISION_ID and COMPANY_ID in GENERAL_LEDGER table

ALTER TABLE GENERAL_LEDGER ADD COMPANY_ID int(10) unsigned DEFAULT NULL AFTER FORM_WORKFLOW_ID,
ADD CONSTRAINT FK_GL_COMPANY_ID FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY (COMPANY_ID);

ALTER TABLE GENERAL_LEDGER ADD DIVISION_ID int(10) unsigned DEFAULT NULL AFTER COMPANY_ID,
ADD CONSTRAINT FK_GL_DIVISION_ID FOREIGN KEY (DIVISION_ID) REFERENCES DIVISION (DIVISION_ID);