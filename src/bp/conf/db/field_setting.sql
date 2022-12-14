
-- Description: SQL file for the creation of COMPANY_FIELD_SETTING
CREATE TABLE `FIELD_SETTING` (
  `FIELD_SETTING_ID` int(10) NOT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `FIELD_NAME` varchar(45) NOT NULL,
  `HIDDEN` tinyint(1) NOT NULL,
  `DELETED` tinyint(1) NOT NULL,
  `CREATED_BY` int(10) DEFAULT NULL,
  `CREATED_DATE` timestamp NULL DEFAULT NULL,
  `UPDATED_BY` int(10) DEFAULT NULL,
  `UPDATED_DATE` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`FIELD_SETTING_ID`),
  KEY `FK_FIELD_SETTING_COMPANY_ID` (`COMPANY_ID`),
  CONSTRAINT `FK_FIELD_SETTING_COMPANY_ID` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`COMPANY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1