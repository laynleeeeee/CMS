
-- Description: SQL script in creating PRODUCT_CODE table.

DROP TABLE IF EXISTS PRODUCT_CODE;

CREATE TABLE `PRODUCT_CODE` (
  `PRODUCT_CODE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `PRODUCT_NAME` varchar(50) NOT NULL,
  `CODE` int(10) unsigned NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`PRODUCT_CODE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;