
-- Description: Insert scripts CMS inventory object types.

SYSTEM echo 'Inserting CMS inventory object types';

insert into OBJECT_TYPE VALUES (10, 'STOCK_ADJUSTMENT', 'STOCK_ADJUSTMENT','eulap.eb.service.StockAdjustmentService', 1, 1, NOW(), 1, NOW());
insert into OBJECT_TYPE VALUES (11, 'STOCK_ADJUSTMENT_OUT', 'STOCK_ADJUSTMENT_ITEM','eulap.eb.service.StockAdjustmentService', 1, 1, NOW(), 1, NOW());
insert into OBJECT_TYPE VALUES (12, 'CASH_SALE_ITEM', 'CASH_SALE_ITEM','eulap.eb.service.CashSaleService', 1, 1, NOW(), 1, NOW());
insert into OBJECT_TYPE VALUES (13, 'CASH_SALE', 'CASH_SALE','eulap.eb.service.CashSaleService', 1, 1, NOW(), 1, NOW());
insert into OBJECT_TYPE VALUES (15, 'ACCOUNT_SALE_ITEM', 'ACCOUNT_SALE_ITEM','eulap.eb.service.ArTransactionService', 1, 1, NOW(), 1, NOW());
insert into OBJECT_TYPE VALUES (16, 'STOCK_ADJUSTMENT_IN', 'STOCK_ADJUSTMENT_ITEM','eulap.eb.service.StockAdjustmentService', 1, 1, NOW(), 1, NOW());
insert into OBJECT_TYPE VALUES (17, 'CASH_SALE_AR_LINE', 'CASH_SALE_AR_LINE','eulap.eb.service.CashSaleService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (19, 'CASH_SALE_RETURN',  'CASH_SALE_RETURN','eulap.eb.service.CashSaleReturnService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (20, 'CASH_SALE_RETURN_ITEM',  'CASH_SALE_RETURN_ITEM','eulap.eb.service.CashSaleReturnService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (21, 'CASH_SALE_EXCHANGE_ITEM',  'CASH_SALE_RETURN_ITEM','eulap.eb.service.CashSaleReturnService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (22, 'ACCOUNT_SALE_RETURN',  'AR_TRANSACTION', 'eulap.eb.service.AccountSalesService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (23, 'ACCOUNT_SALE_RETURN_ITEM',  'ACCOUNT_SALE_ITEM', 'eulap.eb.service.AccountSalesService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (24, 'ACCOUNT_SALE_EXCHANGE_ITEM',  'ACCOUNT_SALE_ITEM', 'eulap.eb.service.AccountSalesService', 1, 1, NOW(), 1, NOW());
insert into OBJECT_TYPE VALUES (25, 'R_TRANSFER_RECEIPT', 'R_TRANSFER_RECEIPT','eulap.eb.service.RTransferReceiptService', 1, 1, NOW(), 1, NOW());
insert into OBJECT_TYPE VALUES (26, 'R_TRANSFER_RECEIPT_ITEM', 'R_TRANSFER_RECEIPT_ITEM','eulap.eb.service.RTransferReceiptService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (32, 'CUSTOMER_ADVANCE_PAYMENT', 'CUSTOMER_ADVANCE_PAYMENT','eulap.eb.service.CustomerAdvancePaymentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (33, 'CUSTOMER_ADVANCE_PAYMENT_ITEM', 'CUSTOMER_ADVANCE_PAYMENT_ITEM','eulap.eb.service.CustomerAdvancePaymentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (34, 'CAP_AR_LINE', 'CAP_AR_LINE','eulap.eb.service.CustomerAdvancePaymentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (38, 'R_PURCHASE_ORDER', 'R_PURCHASE_ORDER','eulap.eb.service.RPurchaseOrderService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (39, 'R_PURCHASE_ORDER_ITEM', 'R_PURCHASE_ORDER_ITEM','eulap.eb.service.RPurchaseOrderService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (40, 'R_RECEIVING_REPORT_ITEM', 'R_RECEIVING_REPORT_ITEM','eulap.eb.service.RReceivingReportService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (53, 'PAID_IN_ADVANCE_DELIVERY', 'CAP_DELIVERY','eulap.eb.service.CAPDeliveryService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (54, 'PAID_IN_ADVANCE_DELIVERY_ITEM', 'CAP_DELIVERY_ITEM','eulap.eb.service.CAPDeliveryService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (55, 'PAID_IN_ADVANCE_DELIVERY_AR_LINE', 'CAP_DELIVERY_AR_LINE','eulap.eb.service.CAPDeliveryService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (58, 'R_RETURN_TO_SUPPLIER_ITEM', 'R_RETURN_TO_SUPPLIER_ITEM','eulap.eb.service.RReturnToSupplierService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (59, 'REPACKING', 'REPACKING','eulap.eb.service.RepackingService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (60, 'REPACKING_ITEM', 'REPACKING_ITEM','eulap.eb.service.RepackingService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (98, 'WAREHOUSE', 'WAREHOUSE', 'eulap.eb.service.WarehouseService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (105, 'SERIAL_ITEM', 'SERIAL_ITEM', 'eulap.eb.service.SerialItemService', 1, 1, now(), 1, now());
INSERT INTO OBJECT_TYPE VALUES (127, 'ACCOUNT_SALES_ORDER', 'AR_TRANSACTION', 'eulap.eb.service.AccountSalesService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (134, 'STOCK_ADJUSTMENT_ITEM', 'STOCK_ADJUSTMENT_ITEM', 'eulap.eb.service.StockAdjustmentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (144, 'CASH_SALE_RETURN_AR_LINE', 'CASH_SALE_RETURN_AR_LINE', 'eulap.eb.service.CashSaleService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (160, 'RECEIVING_REPORT',  'AP_INVOICE', 'eulap.eb.service.RReceivingReportService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (161, 'RETURN_TO_SUPPLIER',  'AP_INVOICE', 'eulap.eb.service.RReturnToSupplierService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (164, 'REPACKING_RAW_MATERIAL',  'REPACKING_RAW_MATERIAL', 'eulap.eb.service.RpItemConversionService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (165, 'REPACKING_FINISHED_GOOD',  'REPACKING_FINISHED_GOOD', 'eulap.eb.service.RpItemConversionService', 1, 1, NOW(), 1, NOW());
