
-- Description: Sql script for creating FLEET_TYPE table

DROP TABLE IF EXISTS FLEET_TYPE;

CREATE TABLE `FLEET_TYPE` (
  `FLEET_TYPE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `FLEET_CATEGORY_ID` int(10) unsigned DEFAULT NULL,
  `NAME` varchar(20) NOT NULL,
  `ACTIVE` tinyint(1) NOT NULL,
  `EB_OBJECT_ID` int(10) unsigned DEFAULT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`FLEET_TYPE_ID`),
  KEY `FK_FLEET_TYPE_EB_OBJECT_ID` (`EB_OBJECT_ID`),
  KEY `FK_FLEET_TYPE_CREATED_BY` (`CREATED_BY`),
  KEY `FK_FLEET_TYPE_UPDATED_BY` (`UPDATED_BY`),
  KEY `FK_FLEET_TYPE_CATEGORY_ID` (`FLEET_CATEGORY_ID`),
  CONSTRAINT `FK_FLEET_TYPE_EB_OBJECT_ID` FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID),
  CONSTRAINT `FK_FLEET_TYPE_CREATED_BY` FOREIGN KEY (`CREATED_BY`) REFERENCES `USER` (`USER_ID`),
  CONSTRAINT `FK_FLEET_TYPE_UPDATED_BY` FOREIGN KEY (`UPDATED_BY`) REFERENCES `USER` (`USER_ID`),
  CONSTRAINT `FK_FLEET_TYPE_CATEGORY_ID` FOREIGN KEY (`FLEET_CATEGORY_ID`) REFERENCES `FLEET_CATEGORY` (`FLEET_CATEGORY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;