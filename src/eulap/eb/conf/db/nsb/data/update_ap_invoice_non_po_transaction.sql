
-- Description	: Update script that will update the amount of AP INVOICE NON PO transction sequence NO = 882;

UPDATE AP_INVOICE SET AMOUNT ='290778.79' WHERE AP_INVOICE_ID ='2632';
UPDATE AP_LINE SET AMOUNT ='290778.79' WHERE AP_LINE_ID ='5341';
