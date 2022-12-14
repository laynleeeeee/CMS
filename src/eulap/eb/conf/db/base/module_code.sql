
-- Description: SQL script in creating MODULE_CODE table.

DROP TABLE IF EXISTS MODULE_CODE;

CREATE TABLE `MODULE_CODE` (
  `MODULE_CODE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `PRODUCT_CODE_ID` int(10) unsigned NOT NULL DEFAULT '1',
  `DESCRIPTION` varchar(250) NOT NULL,
  `CODE` int(10) unsigned NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`MODULE_CODE_ID`),
  KEY `FK_MODULE_CODE_PRODUCT_CODE_ID` (`PRODUCT_CODE_ID`),
  CONSTRAINT `FK_MODULE_CODE_PRODUCT_CODE_ID` FOREIGN KEY (`PRODUCT_CODE_ID`) REFERENCES `PRODUCT_CODE` (`PRODUCT_CODE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;