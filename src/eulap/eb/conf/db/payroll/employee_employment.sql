
-- Description: Create script for EMPLOYEE_EMPLOYMENT Table.

CREATE TABLE EMPLOYEE_EMPLOYMENT(
	EMPLOYEE_EMPLOYMENT_ID INT(10) unsigned NOT NULL AUTO_INCREMENT,
	EMPLOYEE_ID INT(10) unsigned NOT NULL,
	COMPANY_NAME varchar(100) NOT NULL,
	YEAR INT(4) unsigned NOT NULL,
	POSITION varchar(50) DEFAULT NULL,
	SEPARATION_REASON varchar(150) DEFAULT NULL,
	PRIMARY KEY (EMPLOYEE_EMPLOYMENT_ID),
	KEY FK_EMPLOYEE_EMPLOYMENT_EMPLOYEE_ID (EMPLOYEE_ID),
	CONSTRAINT FK_EMPLOYEE_EMPLOYMENT_EMPLOYEE_ID FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE (EMPLOYEE_ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;