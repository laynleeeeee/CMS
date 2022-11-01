
-- Description: Add EB_OJBECT_ID in CUSTOMER_ADVANCE_PAYMENT and related tables

-- Customer Advance Payment
ALTER TABLE CUSTOMER_ADVANCE_PAYMENT ADD EB_OBJECT_ID int(10) unsigned DEFAULT NULL AFTER FORM_WORKFLOW_ID,
ADD CONSTRAINT FK_CUSTOMER_ADVANCE_PAYMENT_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID);

-- Items
ALTER TABLE CUSTOMER_ADVANCE_PAYMENT_ITEM ADD EB_OBJECT_ID int(10) unsigned DEFAULT NULL AFTER CUSTOMER_ADVANCE_PAYMENT_ID,
ADD CONSTRAINT FK_CAPI_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID);

-- Other Charges
ALTER TABLE CAP_AR_LINE ADD EB_OBJECT_ID int(10) unsigned DEFAULT NULL AFTER CUSTOMER_ADVANCE_PAYMENT_ID,
ADD CONSTRAINT FK_CAP_AL_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID);