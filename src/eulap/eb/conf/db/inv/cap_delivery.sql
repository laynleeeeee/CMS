
-- Description: Sql script in creating CAP_DELIVERY table

drop table if exists CAP_DELIVERY;

CREATE TABLE `CAP_DELIVERY` (
  `CAP_DELIVERY_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CUSTOMER_ADVANCE_PAYMENT_ID` int(10) unsigned DEFAULT NULL,
  `CUSTOMER_ADVANCE_PAYMENT_TYPE_ID` int(10) unsigned DEFAULT '1',
  `FORM_WORKFLOW_ID` int(10) unsigned DEFAULT NULL,
  `EB_OBJECT_ID` int(10) unsigned DEFAULT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `AR_CUSTOMER_ID` int(10) unsigned NOT NULL,
  `AR_CUSTOMER_ACCOUNT_ID` int(10) unsigned NOT NULL,
  `CAPD_NUMBER` int(20) unsigned NOT NULL,
  `SALES_INVOICE_NO` varchar(100) DEFAULT NULL,
  `DELIVERY_DATE` date NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`CAP_DELIVERY_ID`),
  KEY `FK_CAPD_COMPANY_ID` (`COMPANY_ID`),
  KEY `FK_CAPD_CUSTOMER_ID` (`AR_CUSTOMER_ID`),
  KEY `FK_CAPD_CUSTOMER_ACCOUNT_ID` (`AR_CUSTOMER_ACCOUNT_ID`),
  KEY `FK_CAPD_FORM_WORKFLOW_ID` (`FORM_WORKFLOW_ID`),
  KEY `FK_CAPD_CUSTOMER_ADVANCE_PAYMENT_ID` (`CUSTOMER_ADVANCE_PAYMENT_ID`),
  KEY `FK_CAPD_CREATED_BY` (`CREATED_BY`),
  KEY `FK_CAPD_UPDATED_BY` (`UPDATED_BY`),
  KEY `FK_CAPD_CAP_TYPE_ID` (`CUSTOMER_ADVANCE_PAYMENT_TYPE_ID`),
  KEY `FK_CAPD_EB_OBJECT_ID` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_CAPD_CAP_TYPE_ID` FOREIGN KEY (`CUSTOMER_ADVANCE_PAYMENT_TYPE_ID`) REFERENCES `CUSTOMER_ADVANCE_PAYMENT_TYPE` (`CUSTOMER_ADVANCE_PAYMENT_TYPE_ID`),
  CONSTRAINT `FK_CAPD_COMPANY_ID` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`COMPANY_ID`),
  CONSTRAINT `FK_CAPD_CREATED_BY` FOREIGN KEY (`CREATED_BY`) REFERENCES `USER` (`USER_ID`),
  CONSTRAINT `FK_CAPD_CUSTOMER_ACCOUNT_ID` FOREIGN KEY (`AR_CUSTOMER_ACCOUNT_ID`) REFERENCES `AR_CUSTOMER_ACCOUNT` (`AR_CUSTOMER_ACCOUNT_ID`),
  CONSTRAINT `FK_CAPD_CUSTOMER_ID` FOREIGN KEY (`AR_CUSTOMER_ID`) REFERENCES `AR_CUSTOMER` (`AR_CUSTOMER_ID`),
  CONSTRAINT `FK_CAPD_EB_OBJECT_ID` FOREIGN KEY (`EB_OBJECT_ID`) REFERENCES `EB_OBJECT` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_CAPD_FORM_WORKFLOW_ID` FOREIGN KEY (`FORM_WORKFLOW_ID`) REFERENCES `FORM_WORKFLOW` (`FORM_WORKFLOW_ID`),
  CONSTRAINT `FK_CAPD_UPDATED_BY` FOREIGN KEY (`UPDATED_BY`) REFERENCES `USER` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;