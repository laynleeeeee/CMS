
-- Description: Insert scripts to insert Object Types.

SYSTEM echo 'Inserting OBJECT_TYPE';

insert into OBJECT_TYPE VALUES (1, 'AP_INVOICE', 'AP_INVOICE', 'eulap.eb.service.APInvoiceService', 1, 1, now(), 1, now());

insert into OBJECT_TYPE VALUES (2, 'AP INVOICE LINE', 'AP_INVOICE_LINE','eulap.eb.service.APInvoiceService', 1, 1, now(), 1, now());

insert into OBJECT_TYPE VALUES (3, 'R_RECEIVING_REPORT_RM_ITEM', 'R_RECEIVING_REPORT_RM_ITEM','eulap.eb.service.RrRawMaterialService', 1, 1, now(), 1, now());

INSERT INTO OBJECT_TYPE VALUES (4, 'PROCESSING_REPORT', 'PROCESSING_REPORT','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (5, 'PR_RAW_MATERIALS_ITEM', 'PR_RAW_MATERIALS_ITEM','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (6, 'PR_OTHER_MATERIALS_ITEM', 'PR_OTHER_MATERIALS_ITEM','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (7, 'PR_OTHER_CHARGE', 'PR_OTHER_CHARGE','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (8, 'PR_MAIN_PRODUCT', 'PR_MAIN_PRODUCT','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (9, 'PR_BY_PRODUCT', 'PR_BY_PRODUCT','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());

insert into OBJECT_TYPE VALUES (10, 'STOCK_ADJUSTMENT', 'STOCK_ADJUSTMENT','eulap.eb.service.StockAdjustmentService', 1, 1, NOW(), 1, NOW());

insert into OBJECT_TYPE VALUES (11, 'STOCK_ADJUSTMENT_OUT', 'STOCK_ADJUSTMENT_ITEM','eulap.eb.service.StockAdjustmentService', 1, 1, NOW(), 1, NOW());

insert into OBJECT_TYPE VALUES (12, 'CASH_SALE_ITEM', 'CASH_SALE_ITEM','eulap.eb.service.CashSaleService', 1, 1, NOW(), 1, NOW());

insert into OBJECT_TYPE VALUES (13, 'CASH_SALE', 'CASH_SALE','eulap.eb.service.CashSaleService', 1, 1, NOW(), 1, NOW());

insert into OBJECT_TYPE VALUES (14, 'AR_TRANSACTION', 'AR_TRANSACTION','eulap.eb.service.ArTransactionService', 1, 1, NOW(), 1, NOW());

insert into OBJECT_TYPE VALUES (15, 'ACCOUNT_SALE_ITEM', 'ACCOUNT_SALE_ITEM','eulap.eb.service.ArTransactionService', 1, 1, NOW(), 1, NOW());

insert into OBJECT_TYPE VALUES (16, 'STOCK_ADJUSTMENT_IN', 'STOCK_ADJUSTMENT_ITEM','eulap.eb.service.StockAdjustmentService', 1, 1, NOW(), 1, NOW());

insert into OBJECT_TYPE VALUES (17, 'CASH_SALE_AR_LINE', 'CASH_SALE_AR_LINE','eulap.eb.service.CashSaleService', 1, 1, NOW(), 1, NOW());

insert into OBJECT_TYPE VALUES (18, 'AR_LINE', 'AR_LINE','eulap.eb.service.ArTransactionService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (19, 'CASH_SALE_RETURN',  'CASH_SALE_RETURN','eulap.eb.service.CashSaleReturnService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (20, 'CASH_SALE_RETURN_ITEM',  'CASH_SALE_RETURN_ITEM','eulap.eb.service.CashSaleReturnService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (21, 'CASH_SALE_EXCHANGE_ITEM',  'CASH_SALE_RETURN_ITEM','eulap.eb.service.CashSaleReturnService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (22, 'ACCOUNT_SALE_RETURN',  'AR_TRANSACTION', 'eulap.eb.service.AccountSalesService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (23, 'ACCOUNT_SALE_RETURN_ITEM',  'ACCOUNT_SALE_ITEM', 'eulap.eb.service.AccountSalesService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (24, 'ACCOUNT_SALE_EXCHANGE_ITEM',  'ACCOUNT_SALE_ITEM', 'eulap.eb.service.AccountSalesService', 1, 1, NOW(), 1, NOW());

insert into OBJECT_TYPE VALUES (25, 'R_TRANSFER_RECEIPT', 'R_TRANSFER_RECEIPT','eulap.eb.service.RTransferReceiptService', 1, 1, NOW(), 1, NOW());

insert into OBJECT_TYPE VALUES (26, 'R_TRANSFER_RECEIPT_ITEM', 'R_TRANSFER_RECEIPT_ITEM','eulap.eb.service.RTransferReceiptService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (27, 'PR_MILLING_ORDER', 'PROCESSING_REPORT','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (28, 'PR_PASS_IN', 'PROCESSING_REPORT','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (29, 'PR_PASS_OUT', 'PROCESSING_REPORT','eulap.eb.service.processing.ProcessingReportService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (30, 'REFERENCE_DOCUMENT', 'REFERENCE_DOCUMENT','eulap.eb.service.ReferenceDocumentService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (31, 'PAYROLL', 'PAYROLL', 'eulap.eb.service.payroll.PayrollService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (32, 'CUSTOMER_ADVANCE_PAYMENT', 'CUSTOMER_ADVANCE_PAYMENT','eulap.eb.service.CustomerAdvancePaymentService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (33, 'CUSTOMER_ADVANCE_PAYMENT_ITEM', 'CUSTOMER_ADVANCE_PAYMENT_ITEM','eulap.eb.service.CustomerAdvancePaymentService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (34, 'CAP_AR_LINE', 'CAP_AR_LINE','eulap.eb.service.CustomerAdvancePaymentService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (38, 'R_PURCHASE_ORDER', 'R_PURCHASE_ORDER','eulap.eb.service.RPurchaseOrderService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (39, 'R_PURCHASE_ORDER_ITEM', 'R_PURCHASE_ORDER_ITEM','eulap.eb.service.RPurchaseOrderService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (40, 'R_RECEIVING_REPORT_ITEM', 'R_RECEIVING_REPORT_ITEM','eulap.eb.service.RReceivingReportService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (41, 'ITEM_CONVERSION', 'ITEM_CONVERSION','eulap.eb.service.ItemConversionService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (42, 'ITEM_CONVERSION_LINE', 'ITEM_CONVERSION_LINE','eulap.eb.service.ItemConversionService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (43, 'SALES_ORDER', 'SALES_ORDER','eulap.eb.service.SalesOrderService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (44, 'CSI_RAW_MATERIAL', 'CSI_RAW_MATERIAL','eulap.eb.service.CashSaleProcessingService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (45, 'CSI_FINISHED_PRODUCT', 'CSI_FINISHED_PRODUCT','eulap.eb.service.CashSaleProcessingService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (46, 'WIP_SPECIAL_ORDER', 'WIP_SPECIAL_ORDER','eulap.eb.service.WipSpecialOrderService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (47, 'WIPSO_MATERIAL', 'WIPSO_MATERIAL','eulap.eb.service.WipSpecialOrderService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (48, 'WIPSO_FINISHED_PRODUCT', 'WIPSO_FINISHED_PRODUCT','eulap.eb.service.WipSpecialOrderService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (49, 'WIPSO_OTHER_CHARGE', 'WIPSO_OTHER_CHARGE','eulap.eb.service.WipSpecialOrderService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (53, 'PAID_IN_ADVANCE_DELIVERY', 'CAP_DELIVERY','eulap.eb.service.CAPDeliveryService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (54, 'PAID_IN_ADVANCE_DELIVERY_ITEM', 'CAP_DELIVERY_ITEM','eulap.eb.service.CAPDeliveryService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (55, 'PAID_IN_ADVANCE_DELIVERY_AR_LINE', 'CAP_DELIVERY_AR_LINE','eulap.eb.service.CAPDeliveryService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (56, 'WIP_BAKING', 'PROCESSING_REPORT','eulap.eb.service.processing.ProductionReportService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (57, 'PRODUCTION_REPORT', 'PROCESSING_REPORT','eulap.eb.service.processing.ProductionReportService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (58, 'R_RETURN_TO_SUPPLIER_ITEM', 'R_RETURN_TO_SUPPLIER_ITEM','eulap.eb.service.RReturnToSupplierService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (59, 'REPACKING', 'REPACKING','eulap.eb.service.RepackingService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (60, 'REPACKING_ITEM', 'REPACKING_ITEM','eulap.eb.service.RepackingService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (98, 'WAREHOUSE', 'WAREHOUSE', 'eulap.eb.service.WarehouseService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (99, 'AR_CUSTOMER', 'AR_CUSTOMER', 'eulap.eb.service.ArCustomerService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (100, 'ACCOUNT', 'ACCOUNT', 'eulap.eb.service.AccountService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (101, 'DIVISION', 'DIVISION', 'eulap.eb.service.DivisionService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (102, 'EMPLOYEE_DEDUCTION', 'EMPLOYEE_DEDUCTION', 'eulap.eb.service.PayrollService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (126, 'EMPLOYEE_DOCUMENT', 'EMPLOYEE_DOCUMENT', 'eulap.eb.service.EmployeeDocumentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (127, 'ACCOUNT_SALES_ORDER', 'AR_TRANSACTION', 'eulap.eb.service.AccountSalesService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (128, 'SALARY_TYPE', 'SALARY_TYPE', 'eulap.eb.service.SalaryTypeService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (129, 'AP_PAYMENT', 'AP_PAYMENT', 'eulap.eb.service.ApPaymentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (130, 'GeneralLedger', 'GENERAL_LEDGER', 'eulap.eb.service.GeneralLedgerService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (131, 'AR_MISCELLANEOUS', 'AR_MISCELLANEOUS', 'eulap.eb.service.ArMiscellaneousService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (134, 'STOCK_ADJUSTMENT_ITEM', 'STOCK_ADJUSTMENT_ITEM', 'eulap.eb.service.StockAdjustmentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (135, 'ITEM_BAG_QUANTITY', 'ITEM_BAG_QUANTITY', 'N/A', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (136, 'RRI_BAG_QUANTITY', 'RRI_BAG_QUANTITY', 'eulap.eb.service.RrRawMaterialService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (137, 'RRI_BAG_DISCOUNT', 'RRI_BAG_DISCOUNT', 'eulap.eb.service.RrRawMaterialService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (140, 'GL_ENTRY', 'GL_ENTRY', 'eulap.eb.service.GeneralLedgerService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (141, 'AR_MISCELLANEOUS_LINE', 'AR_MISCELLANEOUS_LINE', 'eulap.eb.service.ArMiscellaneousService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (142, 'AP_PAYMENT_INVOICE', 'AP_PAYMENT_INVOICE', 'eulap.eb.service.ApPaymentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (143, 'AC_AR_LINE', 'AC_AR_LINE', 'eulap.eb.service.ArReceiptService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (144, 'CASH_SALE_RETURN_AR_LINE', 'CASH_SALE_RETURN_AR_LINE', 'eulap.eb.service.CashSaleService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (145, 'CASH_SALE_RETURN_IS', 'CASH_SALE_RETURN', 'eulap.eb.service.CashSaleReturnIsService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (146, 'CUSTOMER_ADVANCE_PAYMENT_IS', 'CUSTOMER_ADVANCE_PAYMENT', 'eulap.eb.service.CustomerAdvancePaymentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (147, 'CUSTOMER_ADVANCE_PAYMENT_WIP_SO', 'CUSTOMER_ADVANCE_PAYMENT', 'eulap.eb.service.CustomerAdvancePaymentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (148, 'R_TRANSFER_RECEIPT_IS', 'R_TRANSFER_RECEIPT', 'eulap.eb.service.TransferReceiptISService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (149, 'CASH_SALE_IS', 'CASH_SALE', 'eulap.eb.service.CashSaleISService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (150, 'PAID_IN_ADVANCE_DELIVERY_IS', 'CAP_DELIVERY', 'eulap.eb.service.CAPDeliveryService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (151, 'PAID_IN_ADVANCE_DELIVERY_WIPSO', 'CAP_DELIVERY', 'eulap.eb.service.CAPDeliveryService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (152, 'PAYROLL_EMPLOYEE_TIMESHEET', 'PAYROLL_EMPLOYEE_TIMESHEET', 'eulap.eb.service.TimeSheetService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (153, 'PAYROLL_EMPLOYEE_SALARY', 'PAYROLL_EMPLOYEE_SALARY', 'eulap.eb.service.PayrollService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (154, 'ACCOUNT_SALE_IS', 'AR_TRANSACTION', 'eulap.eb.service.AccountSaleISService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (155, 'ACCOUNT_SALE_RETURN_IS', 'AR_TRANSACTION', 'eulap.eb.service.AccountSaleISService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (156, 'AP_LINE', 'AP_LINE', 'eulap.eb.service.APInvoiceService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (157, 'ACCOUNT_SALE_RETURN_ITEM_IS',  'ACCOUNT_SALE_ITEM', 'eulap.eb.service.AccountSaleISService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (158, 'ACCOUNT_SALE_EXCHANGE_ITEM_IS',  'ACCOUNT_SALE_ITEM', 'eulap.eb.service.AccountSaleISService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (159, 'ACCOUNT_SALE_ITEM_IS',  'ACCOUNT_SALE_ITEM', 'eulap.eb.service.AccountSaleISService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (160, 'RECEIVING_REPORT',  'AP_INVOICE', 'eulap.eb.service.RReceivingReportService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (161, 'RETURN_TO_SUPPLIER',  'AP_INVOICE', 'eulap.eb.service.RReturnToSupplierService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (162, 'RR_RAW_MATERIAL',  'AP_INVOICE', 'eulap.eb.service.RrRawMaterialService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (163, 'RR_NET_WEIGHT',  'AP_INVOICE', 'eulap.eb.service.RrRawMaterialService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (164, 'REPACKING_RAW_MATERIAL',  'REPACKING_RAW_MATERIAL', 'eulap.eb.service.RpItemConversionService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (165, 'REPACKING_FINISHED_GOOD',  'REPACKING_FINISHED_GOOD', 'eulap.eb.service.RpItemConversionService', 1, 1, NOW(), 1, NOW());

INSERT INTO OBJECT_TYPE VALUES (7000, 'DAILY_SHIFT_SCHEDULE', 'DAILY_SHIFT_SCHEDULE', '', 1, 1, NOW(), 1, NOW());

-- OBJECT_TYPE_ID 32 = CUSTOMER_ADVANCE_PAYMENT
-- OBJECT_TYPE_ID 33 = CUSTOMER_ADVANCE_PAYMENT_ITEM
-- OBJECT_TYPE_ID 34 = CAP_AR_LINE
-- source ~/CMS/src/eulap/eb/conf/db/data/insert_cap_object_type_06_28_2016_11_37.sql

-- OBJECT_TYPE_ID 38 = R_PURCHASE_ORDER
-- OBJECT_TYPE_ID 39 = R_PURCHASE_ORDER_ITEM
-- OBJECT_TYPE_ID 40 = R_RECEIVING_REPORT_ITEM
-- source ~/CMS/src/eulap/eb/conf/db/data/insert_rr_po_object_type_08_23_2016_10_21.sql

-- OBJECT_TYPE_ID 41 = ITEM_CONVERSION
-- OBJECT_TYPE_ID 42 = ITEM_CONVERSION_LINE
-- source ~/CMS/src/eulap/eb/conf/db/data/insert_item_conversion_object_type_09_16_2016_11_58.sql

-- OBJECT_TYPE_ID 43 = SALES_ORDER
-- source ~/CMS/src/eulap/eb/conf/db/data/insert_sales_order_object_type_09_21_2016_15_10.sql

-- OBJECT_TYPE_ID 44 = CSI_RAW_MATERIAL
-- OBJECT_TYPE_ID 45 = CSI_FINISHED_PRODUCT
-- source ~/CMS/src/eulap/eb/conf/db/data/insert_csi_rm_fp_object_type_09_24_2016_11_35.sql

-- OBJECT_TYPE_ID 46 = WIP_SPECIAL_ORDER
-- OBJECT_TYPE_ID 47 = WIPSO_MATERIAL
-- OBJECT_TYPE_ID 48 = WIPSO_FINISHED_PRODUCT
-- OBJECT_TYPE_ID 49 = WIPSO_OTHER_CHARGE
-- source ~/CMS/src/eulap/eb/conf/db/data/insert_wip_special_order_object_type_09_24_2016_10_10.sql

-- OBJECT_TYPE_ID 53 = PAID_IN_ADVANCE_DELIVERY
-- OBJECT_TYPE_ID 54 = PAID_IN_ADVANCE_DELIVERY_ITEM
-- OBJECT_TYPE_ID 55 = PAID_IN_ADVANCE_DELIVERY_AR_LINE
-- source ~/CMS/src/eulap/eb/conf/db/data/insert_cap_delivery_object_type_06_28_2016_11_45.sql

