
-- Description: sql script in creating EMPLOYEE_DTR table

DROP TABLE IF EXISTS EMPLOYEE_DTR;

CREATE TABLE EMPLOYEE_DTR (
  EMPLOYEE_DTR_ID INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  EMPLOYEE_ID INT(10) UNSIGNED NOT NULL,
  LOG_TIME timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  LOCATION_ID INT(10) UNSIGNED DEFAULT NULL,
  IS_SYNCHRONIZE tinyint(1),
  ACTIVE TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (EMPLOYEE_DTR_ID),
  KEY FK_EDRT_EMPLOYEE_ID (EMPLOYEE_ID),
  KEY FK_EDRT_LOCATION_ID (LOCATION_ID),
  CONSTRAINT FK_EDRT_EMPLOYEE_ID FOREIGN KEY (EMPLOYEE_ID)
	REFERENCES EMPLOYEE (EMPLOYEE_ID),
  CONSTRAINT FK_EDRT_LOCATION_ID FOREIGN KEY (LOCATION_ID)
	REFERENCES LOCATION (LOCATION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;