
-- Description: SQL script in creating MF_CODE table.

CREATE TABLE `MF_CODE` (
  `MF_CODE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `MODULE_CODE_ID` int(10) unsigned NOT NULL,
  `DESCRIPTION` varchar(200) NOT NULL,
  `CODE` int(10) unsigned NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`MF_CODE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1