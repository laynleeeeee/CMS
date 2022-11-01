
-- Description: sql script in creating ITEM_SRP table.

DROP TABLE IF EXISTS ITEM_SRP;

CREATE TABLE `ITEM_SRP` (
  `ITEM_SRP_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ITEM_ID` int(10) unsigned NOT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `DIVISION_ID` int(10) unsigned NOT NULL,
  `SRP` double  NOT NULL,
  `ACTIVE` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`ITEM_SRP_ID`),
  KEY `FK_SRP_ITEM` (`ITEM_ID`),
  KEY `FK_SRP_COMPANY` (`COMPANY_ID`),
  KEY `FK_SRP_DIVISION` (`DIVISION_ID`),
  CONSTRAINT `FK_SRP_ITEM` FOREIGN KEY (`ITEM_ID`) REFERENCES `ITEM` (`ITEM_ID`),
  CONSTRAINT `FK_SRP_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`COMPANY_ID`),
  CONSTRAINT `FK_SRP_DIVISION` FOREIGN KEY (`DIVISION_ID`) REFERENCES `DIVISION` (`DIVISION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;