
-- Description: sql script in creating R_RECEIVING_REPORT_RM_ITEM table.

DROP TABLE IF EXISTS R_RECEIVING_REPORT_RM_ITEM;

CREATE TABLE `R_RECEIVING_REPORT_RM_ITEM` (
  `R_RECEIVING_REPORT_RM_ITEM_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `R_RECEIVING_REPORT_ITEM_ID` int(10) unsigned NOT NULL,
  `EB_OBJECT_ID` int(10) unsigned DEFAULT NULL,
  `ITEM_BUYING_DISCOUNT_ID` int(10) unsigned DEFAULT NULL,
  `ITEM_BUYING_ADD_ON_ID` int(10) unsigned DEFAULT NULL,
  `DISCOUNT` double DEFAULT NULL,
  `ADD_ON` double DEFAULT NULL,
  `AMOUNT` double DEFAULT NULL,
  PRIMARY KEY (`R_RECEIVING_REPORT_RM_ITEM_ID`),
  KEY `FK_RMI_RR_ITEM_ID` (`R_RECEIVING_REPORT_ITEM_ID`),
  KEY `FK_RMI_IB_DISCOUNT_ID` (`ITEM_BUYING_DISCOUNT_ID`),
  KEY `FK_RMI_IB_ADD_ON_ID` (`ITEM_BUYING_ADD_ON_ID`),
  KEY `FK_RMI_EB_OBJECT_ID` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_RMI_EB_OBJECT_ID` FOREIGN KEY (`EB_OBJECT_ID`) REFERENCES `EB_OBJECT` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_RMI_IB_ADD_ON_ID` FOREIGN KEY (`ITEM_BUYING_ADD_ON_ID`) REFERENCES `ITEM_BUYING_ADD_ON` (`ITEM_BUYING_ADD_ON_ID`),
  CONSTRAINT `FK_RMI_IB_DISCOUNT_ID` FOREIGN KEY (`ITEM_BUYING_DISCOUNT_ID`) REFERENCES `ITEM_BUYING_DISCOUNT` (`ITEM_BUYING_DISCOUNT_ID`),
  CONSTRAINT `FK_RMI_RR_ITEM_ID` FOREIGN KEY (`R_RECEIVING_REPORT_ITEM_ID`) REFERENCES `R_RECEIVING_REPORT_ITEM` (`R_RECEIVING_REPORT_ITEM_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;