
-- Description: sql script in creating ITEM_CONVERSION_LINE table.

DROP TABLE IF EXISTS ITEM_CONVERSION_LINE;

CREATE TABLE `ITEM_CONVERSION_LINE` (
  `ITEM_CONVERSION_LINE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ITEM_CONVERSION_ID` int(10) unsigned NOT NULL,
  `FROM_ITEM_ID` int(10) unsigned NOT NULL,
  `TO_ITEM_ID` int(10) unsigned NOT NULL,
  `EB_OBJECT_ID` int(10) unsigned DEFAULT NULL,
  `QUANTITY` double NOT NULL,
  `UNIT_COST` double NOT NULL DEFAULT '0',
  `CONVERTED_QUANTITY` double NOT NULL,
  `CONVERTED_UNIT_COST` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`ITEM_CONVERSION_LINE_ID`),
  KEY `FK_ICL_ITEM_CONVERSION_ID` (`ITEM_CONVERSION_ID`),
  KEY `FK_ICL_FROM_ITEM_ID` (`FROM_ITEM_ID`),
  KEY `FK_ICL_TO_ITEM_ID` (`TO_ITEM_ID`),
  KEY `FK_ICL_EB_OBJECT_ID` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_ICL_ITEM_CONVERSION_ID` FOREIGN KEY (`ITEM_CONVERSION_ID`) REFERENCES `ITEM_CONVERSION` (`ITEM_CONVERSION_ID`),
  CONSTRAINT `FK_ICL_FROM_ITEM_ID` FOREIGN KEY (`FROM_ITEM_ID`) REFERENCES `ITEM` (`ITEM_ID`),
  CONSTRAINT `FK_ICL_TO_ITEM_ID` FOREIGN KEY (`TO_ITEM_ID`) REFERENCES `ITEM` (`ITEM_ID`),
  CONSTRAINT `FK_ICL_EB_OBJECT_ID` FOREIGN KEY (`EB_OBJECT_ID`) REFERENCES `EB_OBJECT` (`EB_OBJECT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;