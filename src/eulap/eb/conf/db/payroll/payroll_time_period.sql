
-- Description: SQL script in creating the PAYROLL_TIME_PERIOD table

CREATE TABLE `PAYROLL_TIME_PERIOD` (
  `PAYROLL_TIME_PERIOD_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `NAME` varchar(50) NOT NULL,
  `MONTH` int(2) unsigned NOT NULL,
  `YEAR` int(4) unsigned NOT NULL,
  `ACTIVE` tinyint(1) NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`PAYROLL_TIME_PERIOD_ID`),
  KEY FK_PAYROLL_TIME_PERIOD_CREATED_BY (CREATED_BY),
  KEY FK_PAYROLL_TIME_PERIOD_UPDATED_BY (UPDATED_BY),
  CONSTRAINT FK_PAYROLL_TIME_PERIOD_CREATED_BY FOREIGN KEY (CREATED_BY) REFERENCES USER (USER_ID),
  CONSTRAINT FK_PAYROLL_TIME_PERIOD_UPDATED_BY FOREIGN KEY (UPDATED_BY) REFERENCES USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;