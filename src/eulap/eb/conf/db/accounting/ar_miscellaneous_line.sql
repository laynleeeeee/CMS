
-- Description: Sql script in creating AR_MISCELLANEOUS_LINE table

DROP TABLE IF EXISTS AR_MISCELLANEOUS_LINE;

CREATE TABLE `AR_MISCELLANEOUS_LINE` (
  `AR_MISCELLANEOUS_LINE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AR_MISCELLANEOUS_ID` int(10) unsigned NOT NULL,
  `EB_OBJECT_ID` INT(10) unsigned DEFAULT NULL,
  `SERVICE_SETTING_ID` int(10) unsigned NOT NULL,
  `QUANTITY` double DEFAULT '0',
  `UNITOFMEASUREMENT_ID` int(10) unsigned DEFAULT NULL,
  `UP_AMOUNT` double DEFAULT '0',
  `AMOUNT` double DEFAULT '0',
  PRIMARY KEY (`AR_MISCELLANEOUS_LINE_ID`),
  KEY `FK_AR_MISCELLANEOUS_LINE_AR_MISCELLANEOUS_ID` (`AR_MISCELLANEOUS_ID`),
  KEY `FK_AR_MISCELLANEOUS_LINE_SERVICE_SETTING_ID` (`SERVICE_SETTING_ID`),
  KEY `FK_AR_MISCELLANEOUS_LINE_EB_OBJECT_ID` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_AR_MISCELLANEOUS_LINE_SERVICE_SETTING_ID` FOREIGN KEY (`SERVICE_SETTING_ID`) REFERENCES `SERVICE_SETTING` (`SERVICE_SETTING_ID`),
  CONSTRAINT `FK_AR_MISCELLANEOUS_LINE_AR_MISCELLANEOUS_ID` FOREIGN KEY (`AR_MISCELLANEOUS_ID`) REFERENCES `AR_MISCELLANEOUS` (`AR_MISCELLANEOUS_ID`),
  CONSTRAINT `FK_AR_MISCELLANEOUS_LINE_EB_OBJECT_ID` FOREIGN KEY (`EB_OBJECT_ID`) REFERENCES `EB_OBJECT` (`EB_OBJECT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;