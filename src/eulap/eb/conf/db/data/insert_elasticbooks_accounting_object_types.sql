
-- Description: Insert script for CMS accounting object types.

SYSTEM echo 'Inserting CMS accounting object types';

insert into OBJECT_TYPE VALUES (1, 'AP_INVOICE', 'AP_INVOICE', 'eulap.eb.service.APInvoiceService', 1, 1, now(), 1, now());
insert into OBJECT_TYPE VALUES (2, 'AP INVOICE LINE', 'AP_INVOICE_LINE','eulap.eb.service.APInvoiceService', 1, 1, now(), 1, now());
insert into OBJECT_TYPE VALUES (14, 'AR_TRANSACTION', 'AR_TRANSACTION','eulap.eb.service.ArTransactionService', 1, 1, NOW(), 1, NOW());
insert into OBJECT_TYPE VALUES (18, 'AR_LINE', 'AR_LINE','eulap.eb.service.ArTransactionService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (99, 'AR_CUSTOMER', 'AR_CUSTOMER', 'eulap.eb.service.ArCustomerService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (100, 'ACCOUNT', 'ACCOUNT', 'eulap.eb.service.AccountService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (103, 'AR_CUSTOMER_ACCOUNT', 'AR_CUSTOMER_ACCOUNT', 'eulap.eb.service.ArCustomerAcctService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (119, 'AR_RECEIPT', 'AR_RECEIPT', 'eulap.eb.service.ArReceiptService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (129, 'AP_PAYMENT', 'AP_PAYMENT', 'eulap.eb.service.ApPaymentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (130, 'GeneralLedger', 'GENERAL_LEDGER', 'eulap.eb.service.GeneralLedgerService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (131, 'AR_MISCELLANEOUS', 'AR_MISCELLANEOUS', 'eulap.eb.service.ArMiscellaneousService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (140, 'GL_ENTRY', 'GL_ENTRY', 'eulap.eb.service.GeneralLedgerService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (141, 'AR_MISCELLANEOUS_LINE', 'AR_MISCELLANEOUS_LINE', 'eulap.eb.service.ArMiscellaneousService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (142, 'AP_PAYMENT_INVOICE', 'AP_PAYMENT_INVOICE', 'eulap.eb.service.ApPaymentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (143, 'AC_AR_LINE', 'AC_AR_LINE', 'eulap.eb.service.ArReceiptService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (156, 'AP_LINE', 'AP_LINE', 'eulap.eb.service.APInvoiceService', 1, 1, NOW(), 1, NOW());
