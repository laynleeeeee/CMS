
-- Description: Add EB_OJBECT_ID in R_PURCHASE_ORDER table

ALTER TABLE R_PURCHASE_ORDER ADD EB_OBJECT_ID int(10) unsigned DEFAULT NULL AFTER REMARKS,
ADD CONSTRAINT FK_RPO_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID);