
-- Description: sql script in creating R_RETURN_TO_SUPPLIER table.

DROP TABLE IF EXISTS R_RETURN_TO_SUPPLIER;

CREATE TABLE `R_RETURN_TO_SUPPLIER` (
  `R_RETURN_TO_SUPPLIER_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AP_INVOICE_ID` int(10) unsigned DEFAULT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `WAREHOUSE_ID` int(10) unsigned NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`R_RETURN_TO_SUPPLIER_ID`),
  KEY `FK_RRTS_COMPANY_ID` (`COMPANY_ID`),
  KEY `FK_RRTS_WAREHOUSE_ID` (`WAREHOUSE_ID`),
  KEY `FK_RRTS_CREATED_BY` (`CREATED_BY`),
  KEY `FK_RRTS_UPDATED_BY` (`UPDATED_BY`),
  KEY `FK_RRTS_AP_INVOICE_ID` (`AP_INVOICE_ID`),
  CONSTRAINT `FK_RRTS_AP_INVOICE_ID` FOREIGN KEY (`AP_INVOICE_ID`) REFERENCES `AP_INVOICE` (`AP_INVOICE_ID`),
  CONSTRAINT `FK_RRTS_COMPANY_ID` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`COMPANY_ID`),
  CONSTRAINT `FK_RRTS_CREATED_BY` FOREIGN KEY (`CREATED_BY`) REFERENCES `USER` (`USER_ID`),
  CONSTRAINT `FK_RRTS_UPDATED_BY` FOREIGN KEY (`UPDATED_BY`) REFERENCES `USER` (`USER_ID`),
  CONSTRAINT `FK_RRTS_WAREHOUSE_ID` FOREIGN KEY (`WAREHOUSE_ID`) REFERENCES `WAREHOUSE` (`WAREHOUSE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;