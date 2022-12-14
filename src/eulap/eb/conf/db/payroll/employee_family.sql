
-- Description: Create script for EMPLOYEE_FAMILY Table.

CREATE TABLE EMPLOYEE_FAMILY(
	EMPLOYEE_FAMILY_ID INT(10) unsigned NOT NULL AUTO_INCREMENT,
	EMPLOYEE_ID INT(10) unsigned NOT NULL,
	FATHER_NAME varchar(100) NOT NULL,
	MOTHER_NAME varchar(100) NOT NULL,
	SPOUSE_NAME varchar(100) DEFAULT NULL,
	FATHER_OCCUPATION varchar(100) DEFAULT NULL,
	MOTHER_OCCUPATION varchar(100) DEFAULT NULL,
	SPOUSE_OCCUPATION varchar(100) DEFAULT NULL,
	PRIMARY KEY (EMPLOYEE_FAMILY_ID),
	KEY FK_EMPLOYEE_FAMILY_EMPLOYEE_ID (EMPLOYEE_ID),
	CONSTRAINT FK_EMPLOYEE_FAMILY_EMPLOYEE_ID FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE (EMPLOYEE_ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;