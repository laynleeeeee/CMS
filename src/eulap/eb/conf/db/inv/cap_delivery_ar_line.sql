
-- Description: sql script in creating CAP_DELIVERY_AR_LINE table.

drop table if exists CAP_DELIVERY_AR_LINE;

CREATE TABLE `CAP_DELIVERY_AR_LINE` (
  `CAP_DELIVERY_AR_LINE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CAP_DELIVERY_ID` int(10) unsigned NOT NULL,
  `EB_OBJECT_ID` int(10) unsigned DEFAULT NULL,
  `AR_LINE_SETUP_ID` int(10) unsigned NOT NULL,
  `QUANTITY` double DEFAULT '0',
  `UNITOFMEASUREMENT_ID` int(10) unsigned DEFAULT NULL,
  `UP_AMOUNT` double DEFAULT '0',
  `AMOUNT` double DEFAULT '0',
  PRIMARY KEY (`CAP_DELIVERY_AR_LINE_ID`),
  KEY `FK_CAPD_AL_CAPD_ID` (`CAP_DELIVERY_ID`),
  KEY `FK_CAPD_AL_AR_LINE_SETUP_ID` (`AR_LINE_SETUP_ID`),
  KEY `FK_CAPD_AL_EB_OBJECT_ID` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_CADP_AL_CAPD_ID` FOREIGN KEY (`CAP_DELIVERY_ID`) REFERENCES `CAP_DELIVERY` (`CAP_DELIVERY_ID`),
  CONSTRAINT `FK_CAPD_AL_AR_LINE_SETUP_ID` FOREIGN KEY (`AR_LINE_SETUP_ID`) REFERENCES `AR_LINE_SETUP` (`AR_LINE_SETUP_ID`),
  CONSTRAINT `FK_CAPD_AL_EB_OBJECT_ID` FOREIGN KEY (`EB_OBJECT_ID`) REFERENCES `EB_OBJECT` (`EB_OBJECT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;