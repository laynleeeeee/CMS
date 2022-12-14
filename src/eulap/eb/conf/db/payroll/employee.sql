
-- Description: Script for creating employee table.

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS EMPLOYEE;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE EMPLOYEE (
  EMPLOYEE_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
  COMPANY_ID int(10) unsigned NOT NULL,
  DIVISION_ID int(10) unsigned NOT NULL,
  EMPLOYEE_SHIFT_ID int(10) unsigned DEFAULT NULL,
  EMPLOYEE_TYPE_ID int(10) unsigned NOT NULL,
  EMPLOYEE_STATUS_ID int(10) unsigned NOT NULL,
  EMPLOYEE_NO varchar(10) NOT NULL,
  BIOMETRIC_ID int(10) NOT NULL,
  FIRST_NAME varchar(40) NOT NULL,
  MIDDLE_NAME varchar(40) DEFAULT NULL,
  LAST_NAME varchar(40) NOT NULL,
  POSITION_ID int(10) unsigned NOT NULL,
  GENDER int(2) NOT NULL,
  BIRTH_DATE date NOT NULL,
  CIVIL_STATUS int(2) NOT NULL,
  CONTACT_NO varchar(20) DEFAULT NULL,
  ADDRESS varchar(150) NOT NULL,
  EMAIL_ADDRESS varchar(40) DEFAULT NULL,
  ACTIVE tinyint(1) NOT NULL,
  CREATED_BY int(10) unsigned NOT NULL,
  CREATED_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UPDATED_BY int(10) unsigned NOT NULL,
  UPDATED_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (EMPLOYEE_ID),
  KEY FK_EMPLOYEE_COMPANY_ID (COMPANY_ID),
  KEY FK_EMPLOYEE_SHIFT_ID (EMPLOYEE_SHIFT_ID),
  KEY FK_EMPLOYEE_TYPE_ID (EMPLOYEE_TYPE_ID),
  KEY FK_EMPLOYEE_STATUS_ID (EMPLOYEE_STATUS_ID),
  KEY FK_EMPLOYEE_POSITION_ID (POSITION_ID),
  KEY FK_EMPLOYEE_CREATED_BY (CREATED_BY),
  KEY FK_EMPLOYEE_UPDATED_BY (UPDATED_BY),
  KEY FK_EMPLOYEE_DIVISION_ID (DIVISION_ID),
  CONSTRAINT FK_EMPLOYEE_COMPANY_ID FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY (COMPANY_ID),
  CONSTRAINT FK_EMPLOYEE_CREATED_BY FOREIGN KEY (CREATED_BY) REFERENCES USER (USER_ID),
  CONSTRAINT FK_EMPLOYEE_DIVISION_ID FOREIGN KEY (DIVISION_ID) REFERENCES DIVISION (DIVISION_ID),
  CONSTRAINT FK_EMPLOYEE_POSITION_ID FOREIGN KEY (POSITION_ID) REFERENCES POSITION (POSITION_ID),
  CONSTRAINT FK_EMPLOYEE_STATUS_ID FOREIGN KEY (EMPLOYEE_STATUS_ID) REFERENCES EMPLOYEE_STATUS (EMPLOYEE_STATUS_ID),
  CONSTRAINT FK_EMPLOYEE_TYPE_ID FOREIGN KEY (EMPLOYEE_TYPE_ID) REFERENCES EMPLOYEE_TYPE (EMPLOYEE_TYPE_ID),
  CONSTRAINT FK_EMPLOYEE_UPDATED_BY FOREIGN KEY (UPDATED_BY) REFERENCES USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;