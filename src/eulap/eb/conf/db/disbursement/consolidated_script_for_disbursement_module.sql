
-- Description	: Consolidated script for requisition form to withdrawal slip create, update, and insert scripts.

ALTER TABLE WITHDRAWAL_SLIP ADD REQUISITION_FORM_ID int(10) unsigned DEFAULT NULL AFTER EB_OBJECT_ID,
ADD CONSTRAINT FK_WITHDRAWAL_SLIP_REQUISITION_FORM_ID FOREIGN KEY (REQUISITION_FORM_ID) REFERENCES REQUISITION_FORM (REQUISITION_FORM_ID);

ALTER TABLE R_PURCHASE_ORDER ADD PR_REFERENCE text DEFAULT NULL AFTER REMARKS;

ALTER TABLE R_RECEIVING_REPORT_ITEM ADD ITEM_DISCOUNT_TYPE_ID int(10) unsigned DEFAULT NULL AFTER VAT_AMOUNT,
ADD CONSTRAINT FK_RRI_ITEM_DISCOUNT_TYPE_ID FOREIGN KEY (ITEM_DISCOUNT_TYPE_ID) REFERENCES ITEM_DISCOUNT_TYPE (ITEM_DISCOUNT_TYPE_ID);
ALTER TABLE R_RECEIVING_REPORT_ITEM ADD DISCOUNT_VALUE double unsigned DEFAULT NULL AFTER ITEM_DISCOUNT_TYPE_ID;
ALTER TABLE R_RECEIVING_REPORT_ITEM ADD DISCOUNT double unsigned DEFAULT NULL AFTER DISCOUNT_VALUE;

ALTER TABLE AP_INVOICE_LINE ADD DISCOUNT_TYPE_ID int(10) unsigned DEFAULT NULL AFTER VAT_AMOUNT,
ADD CONSTRAINT FK_AP_IL_DISCOUNT_TYPE_ID FOREIGN KEY (DISCOUNT_TYPE_ID) REFERENCES ITEM_DISCOUNT_TYPE (ITEM_DISCOUNT_TYPE_ID);
ALTER TABLE AP_INVOICE_LINE ADD DISCOUNT_VALUE double unsigned DEFAULT NULL AFTER DISCOUNT_TYPE_ID;
ALTER TABLE AP_INVOICE_LINE ADD DISCOUNT double unsigned DEFAULT NULL AFTER DISCOUNT_VALUE;