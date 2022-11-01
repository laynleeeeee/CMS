
-- Description: Insert scripts CMS processing object types.

SYSTEM echo 'Inserting CMS processing object types';

insert into OBJECT_TYPE VALUES (3, 'R_RECEIVING_REPORT_RM_ITEM', 'R_RECEIVING_REPORT_RM_ITEM','eulap.eb.service.RrRawMaterialService', 1, 1, now(), 1, now());
INSERT INTO OBJECT_TYPE VALUES (4, 'PROCESSING_REPORT', 'PROCESSING_REPORT','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (5, 'PR_RAW_MATERIALS_ITEM', 'PR_RAW_MATERIALS_ITEM','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (6, 'PR_OTHER_MATERIALS_ITEM', 'PR_OTHER_MATERIALS_ITEM','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (7, 'PR_OTHER_CHARGE', 'PR_OTHER_CHARGE','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (8, 'PR_MAIN_PRODUCT', 'PR_MAIN_PRODUCT','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (9, 'PR_BY_PRODUCT', 'PR_BY_PRODUCT','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (27, 'PR_MILLING_ORDER', 'PROCESSING_REPORT','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (28, 'PR_PASS_IN', 'PROCESSING_REPORT','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (29, 'PR_PASS_OUT', 'PROCESSING_REPORT','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (41, 'ITEM_CONVERSION', 'ITEM_CONVERSION','eulap.eb.service.ItemConversionService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (42, 'ITEM_CONVERSION_LINE', 'ITEM_CONVERSION_LINE','eulap.eb.service.ItemConversionService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (43, 'SALES_ORDER', 'SALES_ORDER','eulap.eb.service.SalesOrderService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (44, 'CSI_RAW_MATERIAL', 'CSI_RAW_MATERIAL','eulap.eb.service.CashSaleProcessingService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (45, 'CSI_FINISHED_PRODUCT', 'CSI_FINISHED_PRODUCT','eulap.eb.service.CashSaleProcessingService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (46, 'WIP_SPECIAL_ORDER', 'WIP_SPECIAL_ORDER','eulap.eb.service.WipSpecialOrderService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (47, 'WIPSO_MATERIAL', 'WIPSO_MATERIAL','eulap.eb.service.WipSpecialOrderService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (48, 'WIPSO_FINISHED_PRODUCT', 'WIPSO_FINISHED_PRODUCT','eulap.eb.service.WipSpecialOrderService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (49, 'WIPSO_OTHER_CHARGE', 'WIPSO_OTHER_CHARGE','eulap.eb.service.WipSpecialOrderService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (56, 'WIP_BAKING', 'PROCESSING_REPORT','eulap.eb.service.processing.ProductionReportService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (57, 'PRODUCTION_REPORT', 'PROCESSING_REPORT','eulap.eb.service.processing.ProductionReportService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (135, 'ITEM_BAG_QUANTITY', 'ITEM_BAG_QUANTITY', 'N/A', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (136, 'RRI_BAG_QUANTITY', 'RRI_BAG_QUANTITY', 'eulap.eb.service.RrRawMaterialService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (137, 'RRI_BAG_DISCOUNT', 'RRI_BAG_DISCOUNT', 'eulap.eb.service.RrRawMaterialService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (145, 'CASH_SALE_RETURN_IS', 'CASH_SALE_RETURN', 'eulap.eb.service.CashSaleReturnIsService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (146, 'CUSTOMER_ADVANCE_PAYMENT_IS', 'CUSTOMER_ADVANCE_PAYMENT', 'eulap.eb.service.CustomerAdvancePaymentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (147, 'CUSTOMER_ADVANCE_PAYMENT_WIP_SO', 'CUSTOMER_ADVANCE_PAYMENT', 'eulap.eb.service.CustomerAdvancePaymentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (148, 'R_TRANSFER_RECEIPT_IS', 'R_TRANSFER_RECEIPT', 'eulap.eb.service.TransferReceiptISService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (149, 'CASH_SALE_IS', 'CASH_SALE', 'eulap.eb.service.CashSaleISService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (150, 'PAID_IN_ADVANCE_DELIVERY_IS', 'CAP_DELIVERY', 'eulap.eb.service.CAPDeliveryService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (151, 'PAID_IN_ADVANCE_DELIVERY_WIPSO', 'CAP_DELIVERY', 'eulap.eb.service.CAPDeliveryService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (154, 'ACCOUNT_SALE_IS', 'AR_TRANSACTION', 'eulap.eb.service.AccountSaleISService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (155, 'ACCOUNT_SALE_RETURN_IS', 'AR_TRANSACTION', 'eulap.eb.service.AccountSaleISService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (157, 'ACCOUNT_SALE_RETURN_ITEM_IS',  'ACCOUNT_SALE_ITEM', 'eulap.eb.service.AccountSaleISService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (158, 'ACCOUNT_SALE_EXCHANGE_ITEM_IS',  'ACCOUNT_SALE_ITEM', 'eulap.eb.service.AccountSaleISService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (159, 'ACCOUNT_SALE_ITEM_IS',  'ACCOUNT_SALE_ITEM', 'eulap.eb.service.AccountSaleISService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (162, 'RR_RAW_MATERIAL',  'AP_INVOICE', 'eulap.eb.service.RrRawMaterialService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (163, 'RR_NET_WEIGHT',  'AP_INVOICE', 'eulap.eb.service.RrRawMaterialService', 1, 1, NOW(), 1, NOW());
