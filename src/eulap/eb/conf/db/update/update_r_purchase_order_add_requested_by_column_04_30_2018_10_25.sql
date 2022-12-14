
-- Description: Sql script to add REQUESTED_BY_ID column in R_PURCHASE_ORDER table.

ALTER TABLE R_PURCHASE_ORDER ADD COLUMN REQUESTED_BY_ID int(10) unsigned DEFAULT NULL AFTER TERM_ID,
ADD CONSTRAINT FK_R_PURCHASE_ORDER_REQUESTED_BY_ID FOREIGN KEY (REQUESTED_BY_ID) REFERENCES EMPLOYEE (EMPLOYEE_ID);