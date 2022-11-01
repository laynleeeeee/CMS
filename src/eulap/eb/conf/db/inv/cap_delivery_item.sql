
-- Description: Sql script in creating CAP_DELIVERY_ITEM table

drop table if exists CAP_DELIVERY_ITEM;

CREATE TABLE `CAP_DELIVERY_ITEM` (
  `CAP_DELIVERY_ITEM_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CAP_DELIVERY_ID` int(10) unsigned NOT NULL,
  `EB_OBJECT_ID` int(10) unsigned DEFAULT NULL,
  `WAREHOUSE_ID` int(10) unsigned NOT NULL,
  `ITEM_ID` int(10) unsigned NOT NULL,
  `QUANTITY` double NOT NULL,
  `ITEM_SRP_ID` int(10) unsigned DEFAULT NULL,
  `SRP` double NOT NULL,
  `ITEM_DISCOUNT_ID` int(10) unsigned DEFAULT NULL,
  `UNIT_COST` double DEFAULT NULL,
  `DISCOUNT` double DEFAULT NULL,
  `ITEM_ADD_ON_ID` int(10) unsigned DEFAULT NULL,
  `AMOUNT` double NOT NULL,
  PRIMARY KEY (`CAP_DELIVERY_ITEM_ID`),
  KEY `FK_CAPDI_CAP_DELIVERY_ID` (`CAP_DELIVERY_ID`),
  KEY `FK_CAPDI_WAREHOUSE_ID` (`WAREHOUSE_ID`),
  KEY `FK_CAPDI_ITEM_ID` (`ITEM_ID`),
  KEY `FK_CAPDI_ITEM_SRP_ID` (`ITEM_SRP_ID`),
  KEY `FK_CAPDI_ITEM_DISCOUNT_ID` (`ITEM_DISCOUNT_ID`),
  KEY `FK_CAPDI_ITEM_ADD_ON_ID` (`ITEM_ADD_ON_ID`),
  KEY `FK_CAPDI_EB_OBJECT_ID` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_CAPDI_CAP_DELIVERY_ID` FOREIGN KEY (`CAP_DELIVERY_ID`) REFERENCES `CAP_DELIVERY` (`CAP_DELIVERY_ID`),
  CONSTRAINT `FK_CAPDI_EB_OBJECT_ID` FOREIGN KEY (`EB_OBJECT_ID`) REFERENCES `EB_OBJECT` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_CAPDI_ITEM_ADD_ON_ID` FOREIGN KEY (`ITEM_ADD_ON_ID`) REFERENCES `ITEM_ADD_ON` (`ITEM_ADD_ON_ID`),
  CONSTRAINT `FK_CAPDI_ITEM_DISCOUNT_ID` FOREIGN KEY (`ITEM_DISCOUNT_ID`) REFERENCES `ITEM_DISCOUNT` (`ITEM_DISCOUNT_ID`),
  CONSTRAINT `FK_CAPDI_ITEM_ID` FOREIGN KEY (`ITEM_ID`) REFERENCES `ITEM` (`ITEM_ID`),
  CONSTRAINT `FK_CAPDI_ITEM_SRP_ID` FOREIGN KEY (`ITEM_SRP_ID`) REFERENCES `ITEM_SRP` (`ITEM_SRP_ID`),
  CONSTRAINT `FK_CAPDI_WAREHOUSE_ID` FOREIGN KEY (`WAREHOUSE_ID`) REFERENCES `WAREHOUSE` (`WAREHOUSE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;