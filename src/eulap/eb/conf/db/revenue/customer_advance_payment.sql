
-- Description: Sql script for creating CUSTOMER_ADVANCE_PAYMENT table

DROP TABLE IF EXISTS `CUSTOMER_ADVANCE_PAYMENT`;
CREATE TABLE `CUSTOMER_ADVANCE_PAYMENT` (
  `CUSTOMER_ADVANCE_PAYMENT_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CUSTOMER_ADVANCE_PAYMENT_TYPE_ID` int(10) unsigned DEFAULT '1',
  `CAP_NUMBER` int(20) unsigned NOT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `RECEIPT_METHOD_ID` int(10) DEFAULT NULL,
  `SALES_ORDER_ID` int(10) unsigned DEFAULT NULL,
  `AR_RECEIPT_TYPE_ID` int(10) unsigned NOT NULL,
  `REF_NUMBER` varchar(20) DEFAULT NULL,
  `AR_CUSTOMER_ID` int(10) unsigned NOT NULL,
  `AR_CUSTOMER_ACCOUNT_ID` int(10) unsigned NOT NULL,
  `SALE_INVOICE_NO` varchar(100) DEFAULT NULL,
  `REFERENCE_NO` varchar(100) DEFAULT NULL,
  `RECEIPT_DATE` date NOT NULL,
  `MATURITY_DATE` date NOT NULL,
  `WT_ACCOUNT_SETTING_ID` int(10) unsigned DEFAULT NULL,
  `WT_AMOUNT` double DEFAULT '0',
  `CASH` double NOT NULL,
  `FORM_WORKFLOW_ID` int(10) unsigned DEFAULT NULL,
  `EB_OBJECT_ID` int(10) unsigned DEFAULT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`CUSTOMER_ADVANCE_PAYMENT_ID`),
  KEY `FK_CUSTOMER_ADVANCE_PAYMENT_COMPANY_ID` (`COMPANY_ID`),
  KEY `FK_CUSTOMER_ADVANCE_PAYMENT_RECEIPT_METHOD_ID` (`RECEIPT_METHOD_ID`),
  KEY `FK_CUSTOMER_ADVANCE_PAYMENT_SALES_ORDER_ID` (`SALES_ORDER_ID`),
  KEY `FK_CUSTOMER_ADVANCE_PAYMENT_TYPE_ID` (`AR_RECEIPT_TYPE_ID`),
  KEY `FK_CUSTOMER_ADVANCE_PAYMENT_CUSTOMER_ID` (`AR_CUSTOMER_ID`),
  KEY `FK_CUSTOMER_ADVANCE_PAYMENT_CUSTOMER_ACCOUNT_ID` (`AR_CUSTOMER_ACCOUNT_ID`),
  KEY `FK_CUSTOMER_ADVANCE_PAYMENT_WT_ACCOUNT_SETTING_ID` (`WT_ACCOUNT_SETTING_ID`),
  KEY `FK_CUSTOMER_ADVANCE_PAYMENT_FORM_WORKFLOW_ID` (`FORM_WORKFLOW_ID`),
  KEY `FK_CUSTOMER_ADVANCE_PAYMENT_CREATED_BY` (`CREATED_BY`),
  KEY `FK_CUSTOMER_ADVANCE_PAYMENT_UPDATED_BY` (`UPDATED_BY`),
  KEY `FK_CUSTOMER_ADVANCE_PAYMENT_EB_OBJECT_ID` (`EB_OBJECT_ID`),
  KEY `FK_CUSTOMER_ADVANCE_PAYMENT_CAP_TYPE_ID` (`CUSTOMER_ADVANCE_PAYMENT_TYPE_ID`),
  CONSTRAINT `FK_CUSTOMER_ADVANCE_PAYMENT_CAP_TYPE_ID` FOREIGN KEY (`CUSTOMER_ADVANCE_PAYMENT_TYPE_ID`) REFERENCES `CUSTOMER_ADVANCE_PAYMENT_TYPE` (`CUSTOMER_ADVANCE_PAYMENT_TYPE_ID`),
  CONSTRAINT `FK_CUSTOMER_ADVANCE_PAYMENT_COMPANY_ID` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`COMPANY_ID`),
  CONSTRAINT `FK_CUSTOMER_ADVANCE_PAYMENT_RECEIPT_METHOD_ID` FOREIGN KEY (`RECEIPT_METHOD_ID`) REFERENCES `RECEIPT_METHOD` (`RECEIPT_METHOD_ID`),
  CONSTRAINT `FK_CUSTOMER_ADVANCE_PAYMENT_SALES_ORDER_ID` FOREIGN KEY (`SALES_ORDER_ID`) REFERENCES `SALES_ORDER` (`SALES_ORDER_ID`),
  CONSTRAINT `FK_CUSTOMER_ADVANCE_PAYMENT_CREATED_BY` FOREIGN KEY (`CREATED_BY`) REFERENCES `USER` (`USER_ID`),
  CONSTRAINT `FK_CUSTOMER_ADVANCE_PAYMENT_CUSTOMER_ACCOUNT_ID` FOREIGN KEY (`AR_CUSTOMER_ACCOUNT_ID`) REFERENCES `AR_CUSTOMER_ACCOUNT` (`AR_CUSTOMER_ACCOUNT_ID`),
  CONSTRAINT `FK_CUSTOMER_ADVANCE_PAYMENT_CUSTOMER_ID` FOREIGN KEY (`AR_CUSTOMER_ID`) REFERENCES `AR_CUSTOMER` (`AR_CUSTOMER_ID`),
  CONSTRAINT `FK_CUSTOMER_ADVANCE_PAYMENT_WT_ACCOUNT_SETTING_ID` FOREIGN KEY (`WT_ACCOUNT_SETTING_ID`) REFERENCES `WT_ACCOUNT_SETTING` (`WT_ACCOUNT_SETTING_ID`),
  CONSTRAINT `FK_CUSTOMER_ADVANCE_PAYMENT_EB_OBJECT_ID` FOREIGN KEY (`EB_OBJECT_ID`) REFERENCES `EB_OBJECT` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_CUSTOMER_ADVANCE_PAYMENT_FORM_WORKFLOW_ID` FOREIGN KEY (`FORM_WORKFLOW_ID`) REFERENCES `FORM_WORKFLOW` (`FORM_WORKFLOW_ID`),
  CONSTRAINT `FK_CUSTOMER_ADVANCE_PAYMENT_TYPE_ID` FOREIGN KEY (`AR_RECEIPT_TYPE_ID`) REFERENCES `AR_RECEIPT_TYPE` (`AR_RECEIPT_TYPE_ID`),
  CONSTRAINT `FK_CUSTOMER_ADVANCE_PAYMENT_UPDATED_BY` FOREIGN KEY (`UPDATED_BY`) REFERENCES `USER` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;