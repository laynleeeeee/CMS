
-- Description	: Update term name column, increase max value to 100 characters.

ALTER TABLE TERM MODIFY NAME VARCHAR(100) DEFAULT NULL;