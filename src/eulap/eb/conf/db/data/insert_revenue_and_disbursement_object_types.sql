
-- Description	: Insert script to add new object type for revenue and disbursement module

INSERT INTO OBJECT_TYPE VALUES ('110', 'DELIVERY_RECEIPT', 'DELIVERY_RECEIPT', 'eulap.eb.service.DeliveryReceiptService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('111', 'DELIVERY_RECEIPT_ITEM', 'DELIVERY_RECEIPT_ITEM', 'eulap.eb.service.DeliveryReceiptService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('3003', 'REQUISITION_FORM_ITEM', 'REQUISITION_FORM_ITEM','eulap.eb.service.RequisitionFormService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES ('3026', 'REQUISITION_FORM', 'REQUISITION_FORM','eulap.eb.service.RequisitionFormService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES ('3036', 'PURCHASE_REQUISITION_ITEM', 'PURCHASE_REQUISITION_ITEM','eulap.eb.service.RequisitionFormService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES ('12000', 'SALES_QUOTATION', 'SALES_QUOTATION', 'eulap.eb.service.SalesQuotationService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12001', 'SALES_QUOTATION_ITEM', 'SALES_QUOTATION_ITEM', 'eulap.eb.service.SalesQuotationService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12002', 'SALES_QUOTATION_LINE', 'SALES_QUOTATION_LINE', 'eulap.eb.service.SalesQuotationService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12003', 'SALES_ORDER', 'SALES_ORDER', 'eulap.eb.service.SalesOrderService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12004', 'SALES_ORDER_ITEM', 'SALES_ORDER_ITEM', 'eulap.eb.service.SalesOrderService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12005', 'SALES_ORDER_LINE', 'SALES_ORDER_LINE', 'eulap.eb.service.SalesOrderService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12006', 'AUTHORITY_TO_WITHDRAW', 'AUTHORITY_TO_WITHDRAW', 'eulap.eb.service.AuthorityToWithdrawService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12007', 'AUTHORITY_TO_WITHDRAW_ITEM', 'AUTHORITY_TO_WITHDRAW_ITEM', 'eulap.eb.service.AuthorityToWithdrawService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12008', 'DELIVERY_RECEIPT_LINE', 'DELIVERY_RECEIPT_LINE', 'eulap.eb.service.DeliveryReceiptService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12009', 'AR_INVOICE', 'AR_INVOICE', 'eulap.eb.service.ArInvoiceService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12010', 'AR_INVOICE_ITEM', 'AR_INVOICE_ITEM', 'eulap.eb.service.ArInvoiceService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12011', 'AR_INVOICE_LINE', 'AR_INVOICE_LINE', 'eulap.eb.service.ArInvoiceService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12012', 'AR_RECEIPT_ADVANCE_PAYMENT', 'AR_RECEIPT_ADVANCE_PAYMENT', 'eulap.eb.service.ArReceiptService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12013', 'WORK_ORDER', 'WORK_ORDER', 'eulap.eb.service.WorkOrderService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12014', 'WORK_ORDER_INSTRUCTION', 'WORK_ORDER_INSTRUCTION', 'eulap.eb.service.WorkOrderService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12015', 'WORK_ORDER_ITEM', 'WORK_ORDER_ITEM', 'eulap.eb.service.WorkOrderService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12016', 'WORK_ORDER_LINE', 'WORK_ORDER_LINE', 'eulap.eb.service.WorkOrderService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12017', 'WAYBILL_LINE', 'WAYBILL_LINE', 'eulap.eb.service.DeliveryReceiptService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12018', 'SALES_QUOTATION_TRUCKING_LINE', 'SALES_QUOTATION_TRUCKING_LINE', 'eulap.eb.service.SalesQuotationService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12019', 'SALES_QUOTATION_EQUIPMENT_LINE', 'SALES_QUOTATION_EQUIPMENT_LINE', 'eulap.eb.service.SalesQuotationService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12020', 'SALES_ORDER_TRUCKING_LINE', 'SALES_ORDER_TRUCKING_LINE', 'eulap.eb.service.SalesOrderService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12021', 'SALES_ORDER_EQUIPMENT_LINE', 'SALES_ORDER_EQUIPMENT_LINE', 'eulap.eb.service.SalesOrderService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12022', 'EQUIPMENT_UTILIZATION_LINE', 'EQUIPMENT_UTILIZATION_LINE', 'eulap.eb.service.DeliveryReceiptService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12023', 'AR_INVOICE_TRUCKING_LINE', 'AR_INVOICE_TRUCKING_LINE', 'eulap.eb.service.ArInvoiceService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12024', 'AUTHORITY_TO_WITHDRAW_LINE', 'AUTHORITY_TO_WITHDRAW_LINE', 'eulap.eb.service.AuthorityToWithdrawService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('12025', 'AP_INVOICE_ITEM', 'AP_INVOICE','eulap.eb.service.ApInvoiceItemService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES ('12026', 'AP_INVOICE_SERVICE', 'AP_INVOICE','eulap.eb.service.ApInvoiceItemService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES ('12027', 'AP_INVOICE_ITEM_LINE', 'AP_INVOICE_ITEM','eulap.eb.service.ApInvoiceItemService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES ('12029', 'AR_INVOICE_EQUIPMENT_LINE', 'AR_INVOICE_EQUIPMENT_LINE','eulap.eb.service.ArInvoiceService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES ('12028', 'WORK_ORDER_PURCHASED_ITEM', 'WORK_ORDER_PURCHASED_ITEM', 'eulap.eb.service.WorkOrderService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('21000', 'PURCHASE_ORDER_LINE', 'PURCHASE_ORDER_LINE', 'eulap.eb.service.RPurchaseOrderService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('24000', 'SUPPLIER_ADVANCE_PAYMENT', 'SUPPLIER_ADVANCE_PAYMENT', 'eulap.eb.service.SupplierAdvPaymentService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('24001', 'SUPPLIER_ADVANCE_PAYMENT_LINE', 'SUPPLIER_ADVANCE_PAYMENT_LINE', 'eulap.eb.service.SupplierAdvPaymentService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('24002', 'AP_INVOICE_GS', 'AP_INVOICE', 'eulap.eb.service.ApInvoiceGsService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('24003', 'AP_INVOICE_GOODS', 'AP_INVOICE_GOODS', 'eulap.eb.service.ApInvoiceGsService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('24004', 'AP_PAYMENT_LINE', 'AP_PAYMENT_LINE', 'eulap.eb.service.ApPaymentService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('24005', 'CUSTOMER_ADVANCE_PAYMENT_LINE', 'CUSTOMER_ADVANCE_PAYMENT_LINE', 'eulap.eb.service.CustomerAdvancePaymentService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('24006', 'AR_RECEIPT_LINE', 'AR_RECEIPT_LINE', 'eulap.eb.service.ArReceiptService', '1', '1', NOW(), '1', NOW());
INSERT INTO OBJECT_TYPE VALUES ('24007', 'AR_SERVICE_LINE', 'AR_SERVICE_LINE', 'eulap.eb.service.ArServiceLineService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES ('24008', 'PETTY_CASH_VOUCHER', 'PETTY_CASH_VOUCHER', 'eulap.eb.service.PettyCashVoucherService', 1, 1, NOW(), 1, NOW());

-- TODO: TO DEPRICATE THIS, ADDING THIS AT THE MOMENT TO AVOID ERROR
UPDATE OBJECT_TYPE SET SERVICE_CLASS = 'eulap.eb.service.JyeiWithdrawalSlipService' WHERE OBJECT_TYPE_ID = 96;
