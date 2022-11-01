
-- Description	: Add project retention type id.

ALTER TABLE PROJECT_RETENTION ADD PROJECT_RETENTION_TYPE_ID int(10) unsigned DEFAULT NULL,
ADD CONSTRAINT FK_PR_PROJECT_RETENTION_TYPE_ID FOREIGN KEY (PROJECT_RETENTION_TYPE_ID) REFERENCES PROJECT_RETENTION_TYPE (PROJECT_RETENTION_TYPE_ID);