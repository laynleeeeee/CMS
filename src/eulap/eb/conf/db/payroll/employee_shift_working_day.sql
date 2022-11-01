
-- Description: Script for creating employee shift working day table.

DROP TABLE IF EXISTS EMPLOYEE_SHIFT_WORKING_DAY;

CREATE TABLE EMPLOYEE_SHIFT_WORKING_DAY (
	EMPLOYEE_SHIFT_WORKING_DAY_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	EMPLOYEE_SHIFT_ID int(10) unsigned NOT NULL,
	DAY_OF_THE_WEEK tinyint(1) NOT NULL,
	PRIMARY KEY (EMPLOYEE_SHIFT_WORKING_DAY_ID),
	KEY FK_ESWD_EMPLOYEE_SHIFT_ID (EMPLOYEE_SHIFT_ID),
	CONSTRAINT FK_ESWD_EMPLOYEE_SHIFT_ID FOREIGN KEY (EMPLOYEE_SHIFT_ID) REFERENCES EMPLOYEE_SHIFT (EMPLOYEE_SHIFT_ID)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;