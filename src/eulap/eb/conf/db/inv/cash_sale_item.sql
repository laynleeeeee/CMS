
-- Description: Sql script for creating CASH_SALE_TABLE table

DROP TABLE IF EXISTS CASH_SALE_ITEM;

CREATE TABLE `CASH_SALE_ITEM` (
  `CASH_SALE_ITEM_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CASH_SALE_ID` int(10) unsigned NOT NULL,
  `EB_OBJECT_ID` int(10) unsigned DEFAULT NULL,
  `WAREHOUSE_ID` int(10) unsigned NOT NULL,
  `ITEM_ID` int(10) unsigned NOT NULL,
  `QUANTITY` double NOT NULL,
  `ITEM_SRP_ID` int(10) unsigned NOT NULL,
  `SRP` double NOT NULL,
  `ITEM_DISCOUNT_ID` int(10) unsigned DEFAULT NULL,
  `UNIT_COST` double NOT NULL DEFAULT '0',
  `DISCOUNT` double DEFAULT '0',
  `ITEM_ADD_ON_ID` int(10) unsigned DEFAULT NULL,
  `AMOUNT` double NOT NULL,
  PRIMARY KEY (`CASH_SALE_ITEM_ID`),
  KEY `FK_CSI_CASH_SALE_ID` (`CASH_SALE_ID`),
  KEY `FK_CSI_WAREHOUSE_ID` (`WAREHOUSE_ID`),
  KEY `FK_CSI_ITEM_ID` (`ITEM_ID`),
  KEY `FK_CSI_ITEM_SRP_ID` (`ITEM_SRP_ID`),
  KEY `FK_CSI_ITEM_DISCOUNT_ID` (`ITEM_DISCOUNT_ID`),
  KEY `FK_CSI_ITEM_ADD_ON_ID` (`ITEM_ADD_ON_ID`),
  KEY `FK_CSI_EB_OBJECT_ID` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_CSI_CASH_SALE_ID` FOREIGN KEY (`CASH_SALE_ID`) REFERENCES `CASH_SALE` (`CASH_SALE_ID`),
  CONSTRAINT `FK_CSI_EB_OBJECT_ID` FOREIGN KEY (`EB_OBJECT_ID`) REFERENCES `EB_OBJECT` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_CSI_ITEM_ADD_ON_ID` FOREIGN KEY (`ITEM_ADD_ON_ID`) REFERENCES `ITEM_ADD_ON` (`ITEM_ADD_ON_ID`),
  CONSTRAINT `FK_CSI_ITEM_DISCOUNT_ID` FOREIGN KEY (`ITEM_DISCOUNT_ID`) REFERENCES `ITEM_DISCOUNT` (`ITEM_DISCOUNT_ID`),
  CONSTRAINT `FK_CSI_ITEM_ID` FOREIGN KEY (`ITEM_ID`) REFERENCES `ITEM` (`ITEM_ID`),
  CONSTRAINT `FK_CSI_ITEM_SRP_ID` FOREIGN KEY (`ITEM_SRP_ID`) REFERENCES `ITEM_SRP` (`ITEM_SRP_ID`),
  CONSTRAINT `FK_CSI_WAREHOUSE_ID` FOREIGN KEY (`WAREHOUSE_ID`) REFERENCES `WAREHOUSE` (`WAREHOUSE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;