
-- Description: SQL script in creating DAILY_SHIFT_SCHEDULE_LINE table

CREATE TABLE `MONTHLY_SHIFT_SCHEDULE_LINE` (
  `MONTHLY_SHIFT_SCHEDULE_LINE_ID` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `MONTHLY_SHIFT_SCHEDULE_ID` INT(10) UNSIGNED NOT NULL,
  `EMPLOYEE_ID` INT(10) UNSIGNED NOT NULL,
  `EMPLOYEE_SHIFT_ID` INT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (`MONTHLY_SHIFT_SCHEDULE_LINE_ID`),
  KEY `FK_MSSL_MONTHLY_SHIFT_SCHEDULE_ID`(`MONTHLY_SHIFT_SCHEDULE_ID`),
  KEY `FK_MSSL_EMPLOYEE_ID`(`EMPLOYEE_ID`),
  KEY `FK_MSSL_EMPLOYEE_SHIFT_ID`(`EMPLOYEE_SHIFT_ID`),
  CONSTRAINT `FK_MSSL_MONTHLY_SHIFT_SCHEDULE_ID` FOREIGN KEY (`MONTHLY_SHIFT_SCHEDULE_ID`) REFERENCES `MONTHLY_SHIFT_SCHEDULE`(`MONTHLY_SHIFT_SCHEDULE_ID`),
  CONSTRAINT `FK_MSSL_EMPLOYEE_ID` FOREIGN KEY (`EMPLOYEE_ID`) REFERENCES `EMPLOYEE`(`EMPLOYEE_ID`),
  CONSTRAINT `FK_MSSL_EMPLOYEE_SHIFT_ID` FOREIGN KEY (`EMPLOYEE_SHIFT_ID`) REFERENCES `EMPLOYEE_SHIFT`(`EMPLOYEE_SHIFT_ID`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;