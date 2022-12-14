
-- Description: sql script of user access rights

DROP TABLE IF EXISTS `USER_ACCESS_RIGHT`;

 CREATE TABLE `USER_ACCESS_RIGHT` (
  `USER_ACCESS_RIGHT_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `USER_ID` INT(10) unsigned NOT NULL,
  `PRODUCT_KEY` INT(20) unsigned NOT NULL,
  `MODULE_KEY` INT(20) unsigned NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`USER_ACCESS_RIGHT_ID`),
  KEY `FK_USER_ACCESS_RIGHT_USER_ID` (`USER_ID`),
  KEY `FK_USER_ACCESS_RIGHT_CREATED_BY` (`CREATED_BY`),
  KEY `FK_USER_ACCESS_RIGHT_UPDATED_BY` (`UPDATED_BY`),
  CONSTRAINT `FK_USER_ACCESS_RIGHT_USER_ID` FOREIGN KEY (`USER_ID`) REFERENCES `USER` (`USER_ID`),
  CONSTRAINT `FK_USER_ACCESS_RIGHT_CREATED_BY` FOREIGN KEY (`CREATED_BY`) REFERENCES `USER` (`USER_ID`),
  CONSTRAINT `FK_USER_ACCESS_RIGHT_UPDATED_BY` FOREIGN KEY (`UPDATED_BY`) REFERENCES `USER` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;