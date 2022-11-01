
-- Description: Create script for CSI_FINISHED_PRODUCT table.

DROP TABLE IF EXISTS CSI_FINISHED_PRODUCT;

CREATE TABLE `CSI_FINISHED_PRODUCT` (
  `CSI_FINISHED_PRODUCT_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CASH_SALE_ID` int(10) unsigned NOT NULL,
  `EB_OBJECT_ID` int(10) unsigned DEFAULT NULL,
  `WAREHOUSE_ID` int(10) unsigned NOT NULL,
  `ITEM_ID` int(10) unsigned NOT NULL,
  `QUANTITY` double NOT NULL,
  `UNIT_COST` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`CSI_FINISHED_PRODUCT_ID`),
  KEY `FK_CSI_FD_CASH_SALE_ID` (`CASH_SALE_ID`),
  KEY `FK_CSI_FD_WAREHOUSE_ID` (`WAREHOUSE_ID`),
  KEY `FK_CSI_FD_ITEM_ID` (`ITEM_ID`),
  KEY `FK_CSI_FD_EB_OBJECT_ID` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_CSI_FD_CASH_SALE_ID` FOREIGN KEY (`CASH_SALE_ID`) REFERENCES `CASH_SALE` (`CASH_SALE_ID`),
  CONSTRAINT `FK_CSI_FD_EB_OBJECT_ID` FOREIGN KEY (`EB_OBJECT_ID`) REFERENCES `EB_OBJECT` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_CSI_FD_ITEM_ID` FOREIGN KEY (`ITEM_ID`) REFERENCES `ITEM` (`ITEM_ID`),
  CONSTRAINT `FK_CSI_FD_WAREHOUSE_ID` FOREIGN KEY (`WAREHOUSE_ID`) REFERENCES `WAREHOUSE` (`WAREHOUSE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;