
-- Description: sql script in creating R_RECEIVING_REPORT_ITEM table.

DROP TABLE IF EXISTS R_RECEIVING_REPORT_ITEM;

 CREATE TABLE `R_RECEIVING_REPORT_ITEM` (
  `R_RECEIVING_REPORT_ITEM_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AP_INVOICE_ID` int(10) unsigned DEFAULT NULL,
  `ITEM_ID` int(10) unsigned NOT NULL,
  `QUANTITY` double NOT NULL,
  `UNIT_COST` double NOT NULL,
  `EB_OBJECT_ID` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`R_RECEIVING_REPORT_ITEM_ID`),
  KEY `FK_RRRI_ITEM_ID` (`ITEM_ID`),
  KEY `FK_RRI_AP_INVOICE_ID` (`AP_INVOICE_ID`),
  KEY `FK_RRI_EB_OBJECT_ID` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_RRI_AP_INVOICE_ID` FOREIGN KEY (`AP_INVOICE_ID`) REFERENCES `AP_INVOICE` (`AP_INVOICE_ID`),
  CONSTRAINT `FK_RRRI_ITEM_ID` FOREIGN KEY (`ITEM_ID`) REFERENCES `ITEM` (`ITEM_ID`),
  CONSTRAINT `FK_RRRI_EB_OBJECT_ID` FOREIGN KEY (`EB_OBJECT_ID`) REFERENCES `EB_OBJECT` (`EB_OBJECT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;