
-- Description	: Consolidated NSB data. This will be run when creating a clean database.  

-- EB_OBJECT

-- OBJECT TYPE
INSERT INTO OBJECT_TYPE VALUES ('24000', 'SUPPLIER_ADVANCE_PAYMENT', 'SUPPLIER_ADVANCE_PAYMENT', 'eulap.eb.service.SupplierAdvPaymentService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('24001', 'SUPPLIER_ADVANCE_PAYMENT_LINE', 'SUPPLIER_ADVANCE_PAYMENT_LINE', 'eulap.eb.service.SupplierAdvPaymentService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('24002', 'AP_INVOICE_GS', 'AP_INVOICE', 'eulap.eb.service.ApInvoiceGsService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('24003', 'AP_INVOICE_GOODS', 'AP_INVOICE_GOODS', 'eulap.eb.service.ApInvoiceGsService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('24004', 'AP_PAYMENT_LINE', 'AP_PAYMENT_LINE', 'eulap.eb.service.ApPaymentService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('24005', 'CUSTOMER_ADVANCE_PAYMENT_LINE', 'CUSTOMER_ADVANCE_PAYMENT_LINE', 'eulap.eb.service.CustomerAdvancePaymentService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('24007', 'AR_SERVICE_LINE', 'AR_SERVICE_LINE', 'eulap.eb.service.ArServiceLineService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES ('24008', 'PETTY_CASH_VOUCHER', 'PETTY_CASH_VOUCHER', 'eulap.eb.service.PettyCashVoucherService', 1, 1, NOW(), 1, NOW());

-- OR TYPE
INSERT INTO OR_TYPE VALUES (64, 'Receiving-Report-Serial-Item-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (104, 'Transfer-Receipt-Serial-Item-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES ('24000', 'Adv-Payment-Object-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES ('24001', 'AP-Invoice-GS-Serial-Item-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES ('24002', 'AP-Invoice-GS-Receiving-Report-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES ('24003', 'AP-Invoice-GS-Child-Obj-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES ('24004', 'AP-Payment-Line-Relationship', 1, 1, now(), 1, now());

-- ACCOUNT

-- ACCOUNT_COMBINATION

-- FORM STATUS
INSERT INTO FORM_STATUS VALUES ('30', 'REVIEWED');
INSERT INTO FORM_STATUS VALUES ('31', 'DRAFTED');

-- Other tables

-- TAX TYPE
INSERT INTO TAX_TYPE VALUES (1, 'VATABLE', 1), (2, 'VAT-EXEMPTED', 1), (3, 'ZERO RATED', 1);
-- NEW TAX TYPE FOR NSB PROJECT - ACCOUNTS PAYABLE (DISABLE IF NOT APPLICABLE)
INSERT INTO TAX_TYPE VALUES (4, 'GOODS', 1), (5, 'SERVICES', 1), (6, 'CAPITAL GOODS', 1), (7, 'NRA', 1);
-- NEW TAX TYPE FOR NSB PROJECT - ACCOUNTS RECEVIABLE (DISABLE IF NOT APPLICABLE)
INSERT INTO TAX_TYPE VALUES (8, 'PRIVATE', 1), (9, 'GOVERNMENT', 1);

-- INVOICE TYPE
INSERT INTO INVOICE_TYPE (INVOICE_TYPE_ID, EB_SL_KEY_ID, NAME, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE) 
VALUES ('13', '1', 'Receiving Report - Central', '1', '1', NOW(), '1', NOW()),
('14', '1', 'Receiving Report - NSB 3', '1', '1', NOW(), '1', NOW()),
('15', '1', 'Receiving Report - NSB 4', '1', '1', NOW(), '1', NOW()),
('16', '1', 'Receiving Report - NSB 5', '1', '1', NOW(), '1', NOW()),
('17', '1', 'Receiving Report - NSB 8', '1', '1', NOW(), '1', NOW()),
('18', '1', 'Receiving Report - NSB 8A', '1', '1', NOW(), '1', NOW()),
('19', '1', 'AP Invoice Non PO - Central', '1', '1', NOW(), '1', NOW()),
('20', '1', 'AP Invoice Non PO - NSB 3', '1', '1', NOW(), '1', NOW()),
('21', '1', 'AP Invoice Non PO - NSB 4', '1', '1', NOW(), '1', NOW()),
('22', '1', 'AP Invoice Non PO - NSB 5', '1', '1', NOW(), '1', NOW()),
('23', '1', 'AP Invoice Non PO - NSB 8', '1', '1', NOW(), '1', NOW()),
('24', '1', 'AP Invoice Non PO - NSB 8A', '1', '1', NOW(), '1', NOW()),
('25', '1', 'AP Invoice Goods/Services - Central', '1', '1', NOW(), '1', NOW()),
('26', '1', 'AP Invoice Goods/Services - NSB 3', '1', '1', NOW(), '1', NOW()),
('27', '1', 'AP Invoice Goods/Services - NSB 4', '1', '1', NOW(), '1', NOW()),
('28', '1', 'AP Invoice Goods/Services - NSB 5', '1', '1', NOW(), '1', NOW()),
('29', '1', 'AP Invoice Goods/Services - NSB 8', '1', '1', NOW(), '1', NOW()),
('30', '1', 'AP Invoice Goods/Services - NSB 8A', '1', '1', NOW(), '1', NOW()),
('31', '1', 'Return To Supplier - Central', '1', '1', NOW(), '1', NOW()),
('32', '1', 'Return To Supplier - NSB 3', '1', '1', NOW(), '1', NOW()),
('33', '1', 'Return To Supplier - NSB 4', '1', '1', NOW(), '1', NOW()),
('34', '1', 'Return To Supplier - NSB 5', '1', '1', NOW(), '1', NOW()),
('35', '1', 'Return To Supplier - NSB 8', '1', '1', NOW(), '1', NOW()),
('36', '1', 'Return To Supplier - NSB 8A', '1', '1', NOW(), '1', NOW());

-- AP PAYMENT LINE TYPE
INSERT INTO AP_PAYMENT_LINE_TYPE VALUES
(1,'INVOICE',1,1,NOW(),1,NOW()),
(2,'SUPPLIER ADVANCE PAYMENT',1,1,NOW(),1,NOW());

-- DELIVERY RECEIPT TYPE
INSERT INTO DELIVERY_RECEIPT_TYPE (DELIVERY_RECEIPT_TYPE_ID, NAME, ACTIVE) 
VALUES ('5', 'Delivery Receipt - Cental', '1'),
('6', 'Delivery Receipt - NSB3', '1'),
('7', 'Delivery Receipt - NSB4', '1'),
('8', 'Delivery Receipt - NSB5', '1'),
('9', 'Delivery Receipt - NSB8', '1'),
('10', 'Delivery Receipt - NSB8A', '1');

-- CUSTOMER ADVANCE PAYMENT TYPE
INSERT INTO CUSTOMER_ADVANCE_PAYMENT_TYPE 
VALUES (6, 'CAP - Central', 1, 1, now(), 1, now()),
(7, 'CAP - NSB 3', 1, 1, now(), 1, now()),
(8, 'CAP - NSB 4', 1, 1, now(), 1, now()),
(9, 'CAP - NSB 5', 1, 1, now(), 1, now()),
(10, 'CAP - NSB 8', 1, 1, now(), 1, now()),
(11, 'CAP - NSB 8A', 1, 1, now(), 1, now());

-- AR INVOICE TYPE
INSERT INTO AR_INVOICE_TYPE 
VALUES ('3', 'AR Invoice - Central', '1'),
('4', 'AR Invoice - NSB 3', '1'),
('5', 'AR Invoice - NSB 4', '1'),
('6', 'AR Invoice - NSB 5', '1'),
('7', 'AR Invoice - NSB 8', '1'),
('8', 'AR Invoice - NSB 8A', '1');

-- AR RECEIPT LINE TYPE
INSERT INTO AR_RECEIPT_LINE_TYPE VALUES
(1,'AR INVOICE',1,1,NOW(),1,NOW()),
(2,'AR TRANSACTION',1,1,NOW(),1,NOW()),
(3,'CUSTOMER ADVANCE PAYMENT',1,1,NOW(),1,NOW());

-- SO_TYPE
INSERT INTO SO_TYPE VALUES (1,'PO',1,1,NOW(),1,NOW());
INSERT INTO SO_TYPE VALUES (2,'PCR',1,1,NOW(),1,NOW());

-- STOCK ADJUSTMENT CLASSIFICATION
INSERT INTO STOCK_ADJUSTMENT_CLASSIFICATION VALUES 
(5, 'Stock Adjustment In - Central', 1, 1, NOW(), 1, NOW()),
(6, 'Stock Adjustment In - NSB 3', 1, 1, NOW(), 1, NOW()),
(7, 'Stock Adjustment In - NSB 4', 1, 1, NOW(), 1, NOW()),
(8, 'Stock Adjustment In - NSB 5', 1, 1, NOW(), 1, NOW()),
(9, 'Stock Adjustment In - NSB 8', 1, 1, NOW(), 1, NOW()),
(10, 'Stock Adjustment In - NSB 8A', 1, 1, NOW(), 1, NOW()),
(11, 'Stock Adjustment Out - Central', 1, 1, NOW(), 1, NOW()),
(12, 'Stock Adjustment Out - NSB 3', 1, 1, NOW(), 1, NOW()),
(13, 'Stock Adjustment Out - NSB 4', 1, 1, NOW(), 1, NOW()),
(14, 'Stock Adjustment Out - NSB 5', 1, 1, NOW(), 1, NOW()),
(15, 'Stock Adjustment Out - NSB 8', 1, 1, NOW(), 1, NOW()),
(16, 'Stock Adjustment Out - NSB 8A', 1, 1, NOW(), 1, NOW());

-- AR TRANSACTION TYPE
INSERT INTO AR_TRANSACTION_TYPE VALUES ('17', '1', 'AR Transaction - Central', '1', '1', NOW(), '1', NOW());
INSERT INTO AR_TRANSACTION_TYPE VALUES ('18', '1', 'AR Transaction - NSB 3', '1', '1', NOW(), '1', NOW());
INSERT INTO AR_TRANSACTION_TYPE VALUES ('19', '1', 'AR Transaction - NSB 4', '1', '1', NOW(), '1', NOW());
INSERT INTO AR_TRANSACTION_TYPE VALUES ('20', '1', 'AR Transaction - NSB 5', '1', '1', NOW(), '1', NOW());
INSERT INTO AR_TRANSACTION_TYPE VALUES ('21', '1', 'AR Transaction - NSB 8', '1', '1', NOW(), '1', NOW());
INSERT INTO AR_TRANSACTION_TYPE VALUES ('22', '1', 'AR Transaction - NSB 8A', '1', '1', NOW(), '1', NOW());

-- TRANSACTION CLASSIFICATION
INSERT INTO TRANSACTION_CLASSIFICATION VALUES 
(1,1,'Regular Transaction',1,1,NOW(),1,NOW()),
(2,1,'Debit Memo',1,1,NOW(),1,NOW()),
(3,1,'Credit Memo',1,1,NOW(),1,NOW());

INSERT INTO INVOICE_TYPE (INVOICE_TYPE_ID, EB_SL_KEY_ID, NAME, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE) 
VALUES ('37', '1', 'AP Invoice Confidential - Central', '1', '1', NOW(), '1', NOW()),
('38', '1', 'AP Invoice Confidential - NSB 3', '1', '1', NOW(), '1', NOW()),
('39', '1', 'AP Invoice Confidential - NSB 4', '1', '1', NOW(), '1', NOW()),
('40', '1', 'AP Invoice Confidential - NSB 5', '1', '1', NOW(), '1', NOW()),
('41', '1', 'AP Invoice Confidential - NSB 8', '1', '1', NOW(), '1', NOW()),
('42', '1', 'AP Invoice Confidential - NSB 8A', '1', '1', NOW(), '1', NOW());

-- AR MISCELLANEOUS TYPE
INSERT INTO AR_MISCELLANEOUS_TYPE VALUES (3,'BANK TRANSFER',1,NOW(),1,NOW());
