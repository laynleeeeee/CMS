
-- Description: sql update script in creating Inventory Stock table.
CREATE TABLE `INVENTORY_STOCK` (
  `INVENTORY_STOCK_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `INVENTORY_SUPPLIER_ID` int(10) unsigned NOT NULL,
  `INVENTORY_ITEM_ID` int(10) unsigned NOT NULL,
  `REFERENCE_NUMBER` varchar(50) NOT NULL,
  `DATE` date NOT NULL,
  `QUANTITY` double NOT NULL,
  `DELETED` tinyint(1) NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`INVENTORY_STOCK_ID`),
  KEY `FK_STOCK_INVENTORY_SUPPLIER_ID` (`INVENTORY_SUPPLIER_ID`),
  KEY `FK_STOCK_INVENTORY_ITEM_ID` (`INVENTORY_ITEM_ID`),
  CONSTRAINT `FK_STOCK_INVENTORY_ITEM_ID` FOREIGN KEY (`INVENTORY_ITEM_ID`) REFERENCES `INVENTORY_ITEM` (`INVENTORY_ITEM_ID`),
  CONSTRAINT `FK_STOCK_INVENTORY_SUPPLIER_ID` FOREIGN KEY (`INVENTORY_SUPPLIER_ID`) REFERENCES `INVENTORY_SUPPLIER` (`INVENTORY_SUPPLIER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1