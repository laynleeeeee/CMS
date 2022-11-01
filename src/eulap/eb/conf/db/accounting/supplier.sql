
-- Description: sql script in creating SUPPLIER

DROP TABLE IF EXISTS SUPPLIER;

CREATE TABLE `SUPPLIER` (
  `SUPPLIER_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `EB_SL_KEY_ID` int(10) unsigned NOT NULL,
  `BUS_REG_TYPE_ID` int(10) unsigned DEFAULT NULL,
  `NAME` varchar(100) NOT NULL,
  `ADDRESS` varchar(150) NOT NULL,
  `CONTACT_PERSON` varchar(50) DEFAULT NULL,
  `CONTACT_NUMBER` varchar(20) DEFAULT NULL,
  `ACTIVE` tinyint(1) NOT NULL,
  `TIN` varchar(20) DEFAULT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`SUPPLIER_ID`),
  KEY `FK_SUPPLIER_EB_SL_KEY_ID` (`EB_SL_KEY_ID`),
  KEY `FK_SUPPLIER_CREATED_BY` (`CREATED_BY`),
  KEY `FK_SUPPLIER_UPDATED_BY` (`UPDATED_BY`),
  KEY `FK_SUPPLIER_BUS_REG_TYPE_ID` (`BUS_REG_TYPE_ID`),
  CONSTRAINT `FK_SUPPLIER_BUS_REG_TYPE_ID` FOREIGN KEY (`BUS_REG_TYPE_ID`) REFERENCES `BUS_REG_TYPE` (`BUS_REG_TYPE_ID`),
  CONSTRAINT `FK_SUPPLIER_CREATED_BY` FOREIGN KEY (`CREATED_BY`) REFERENCES `USER` (`USER_ID`),
  CONSTRAINT `FK_SUPPLIER_EB_SL_KEY_ID` FOREIGN KEY (`EB_SL_KEY_ID`) REFERENCES `EB_SL_KEY` (`EB_SL_KEY_ID`),
  CONSTRAINT `FK_SUPPLIER_UPDATED_BY` FOREIGN KEY (`UPDATED_BY`) REFERENCES `USER` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;