
-- Description: Script for creating EMPLOYEE_SHIFT_ADDITIONAL_PAY table.

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS EMPLOYEE_SHIFT_ADDITIONAL_PAY;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE EMPLOYEE_SHIFT_ADDITIONAL_PAY (
  EMPLOYEE_SHIFT_ADDITIONAL_PAY_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
  EMPLOYEE_SHIFT_ID int(10) unsigned NOT NULL,
  WEEKEND_MULTIPLIER double DEFAULT NULL,
  HOLIDAY_MULTIPLIER double DEFAULT NULL,
  PRIMARY KEY (EMPLOYEE_SHIFT_ADDITIONAL_PAY_ID),
  KEY FK_ES_ID (EMPLOYEE_SHIFT_ID),
  CONSTRAINT FK_ES_ID FOREIGN KEY (EMPLOYEE_SHIFT_ID) REFERENCES EMPLOYEE_SHIFT (EMPLOYEE_SHIFT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;