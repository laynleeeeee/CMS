
-- Description: Sql script in creating AR_MISCELLANEOUS_TYPE table

DROP TABLE IF EXISTS AR_MISCELLANEOUS_TYPE;

CREATE TABLE `AR_MISCELLANEOUS_TYPE` (
  `AR_MISCELLANEOUS_TYPE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `NAME` varchar(20) NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`AR_MISCELLANEOUS_TYPE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;