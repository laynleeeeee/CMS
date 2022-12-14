
-- Description: sql script in creating ACCOUNT table.

DROP TABLE IF EXISTS ACCOUNT;

CREATE TABLE `ACCOUNT` (
  `ACCOUNT_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `NUMBER` varchar(10) NOT NULL,
  `ACCOUNT_NAME` varchar(100) NOT NULL,
  `DESCRIPTION` varchar(200) NOT NULL,
  `ACCOUNT_TYPE_ID` int(10) unsigned NOT NULL,
  `RELATED_ACCOUNT_ID` int(10) unsigned DEFAULT NULL,
  `PARENT_ACCOUNT_ID` int(10) unsigned DEFAULT NULL,
  `EB_OBJECT_ID` int(10) unsigned DEFAULT NULL,
  `ACTIVE` tinyint(1) NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `EB_SL_KEY_ID` int(10) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`ACCOUNT_ID`),
  KEY `FK_ACCOUNT_ACCOUNT_TYPE` (`ACCOUNT_TYPE_ID`),
  KEY `FK_ACCOUNT_EB_OBJECT_ID` (`EB_OBJECT_ID`),
  KEY `FK_ACCOUNT_CREATED_BY` (`CREATED_BY`),
  KEY `FK_ACCOUNT_UPDATED_BY` (`UPDATED_BY`),
  KEY `FK_ACCOUNT_EB_SL_KEY_ID` (`EB_SL_KEY_ID`),
  CONSTRAINT `FK_ACCOUNT_EB_SL_KEY_ID` FOREIGN KEY (`EB_SL_KEY_ID`) REFERENCES `EB_SL_KEY` (`EB_SL_KEY_ID`),
  CONSTRAINT `FK_ACCOUNT_EB_OBJECT_ID` FOREIGN KEY (`EB_OBJECT_ID`) REFERENCES `EB_OBJECT` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_ACCOUNT_ACCOUNT_TYPE` FOREIGN KEY (`ACCOUNT_TYPE_ID`) REFERENCES `ACCOUNT_TYPE` (`ACCOUNT_TYPE_ID`),
  CONSTRAINT `FK_ACCOUNT_CREATED_BY` FOREIGN KEY (`CREATED_BY`) REFERENCES `USER` (`USER_ID`),
  CONSTRAINT `FK_ACCOUNT_UPDATED_BY` FOREIGN KEY (`UPDATED_BY`) REFERENCES `USER` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;