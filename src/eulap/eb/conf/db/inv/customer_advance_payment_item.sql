
-- Description: Sql script for creating CUSTOMER_ADVANCE_PAYMENT_TABLE table

DROP TABLE IF EXISTS CUSTOMER_ADVANCE_PAYMENT_ITEM;
CREATE TABLE `CUSTOMER_ADVANCE_PAYMENT_ITEM` (
  `CUSTOMER_ADVANCE_PAYMENT_ITEM_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CUSTOMER_ADVANCE_PAYMENT_ID` int(10) unsigned NOT NULL,
  `EB_OBJECT_ID` int(10) unsigned DEFAULT NULL,
  `WAREHOUSE_ID` int(10) unsigned NOT NULL,
  `ITEM_ID` int(10) unsigned NOT NULL,
  `QUANTITY` double NOT NULL,
  `ITEM_SRP_ID` int(10) unsigned NOT NULL,
  `SRP` double NOT NULL,
  `ITEM_DISCOUNT_ID` int(10) unsigned DEFAULT NULL,
  `UNIT_COST` double DEFAULT '0',
  `DISCOUNT` double DEFAULT '0',
  `ITEM_ADD_ON_ID` int(10) unsigned DEFAULT NULL,
  `AMOUNT` double NOT NULL,
  PRIMARY KEY (`CUSTOMER_ADVANCE_PAYMENT_ITEM_ID`),
  KEY `FK_CAPI_CUSTOMER_ADVANCE_PAYMENT_ID` (`CUSTOMER_ADVANCE_PAYMENT_ID`),
  KEY `FK_CAPI_WAREHOUSE_ID` (`WAREHOUSE_ID`),
  KEY `FK_CAPI_ITEM_ID` (`ITEM_ID`),
  KEY `FK_CAPI_ITEM_SRP_ID` (`ITEM_SRP_ID`),
  KEY `FK_CAPI_ITEM_DISCOUNT_ID` (`ITEM_DISCOUNT_ID`),
  KEY `FK_CAPI_ITEM_ADD_ON_ID` (`ITEM_ADD_ON_ID`),
  KEY `FK_CAPI_EB_OBJECT_ID` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_CAPI_CUSTOMER_ADVANCE_PAYMENT_ID` FOREIGN KEY (`CUSTOMER_ADVANCE_PAYMENT_ID`) REFERENCES `CUSTOMER_ADVANCE_PAYMENT` (`CUSTOMER_ADVANCE_PAYMENT_ID`),
  CONSTRAINT `FK_CAPI_EB_OBJECT_ID` FOREIGN KEY (`EB_OBJECT_ID`) REFERENCES `EB_OBJECT` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_CAPI_ITEM_ADD_ON_ID` FOREIGN KEY (`ITEM_ADD_ON_ID`) REFERENCES `ITEM_ADD_ON` (`ITEM_ADD_ON_ID`),
  CONSTRAINT `FK_CAPI_ITEM_DISCOUNT_ID` FOREIGN KEY (`ITEM_DISCOUNT_ID`) REFERENCES `ITEM_DISCOUNT` (`ITEM_DISCOUNT_ID`),
  CONSTRAINT `FK_CAPI_ITEM_ID` FOREIGN KEY (`ITEM_ID`) REFERENCES `ITEM` (`ITEM_ID`),
  CONSTRAINT `FK_CAPI_ITEM_SRP_ID` FOREIGN KEY (`ITEM_SRP_ID`) REFERENCES `ITEM_SRP` (`ITEM_SRP_ID`),
  CONSTRAINT `FK_CAPI_WAREHOUSE_ID` FOREIGN KEY (`WAREHOUSE_ID`) REFERENCES `WAREHOUSE` (`WAREHOUSE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;