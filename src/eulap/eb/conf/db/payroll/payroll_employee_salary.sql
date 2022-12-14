
-- Description: SQL script in creating the PAYROLL_EMPLOYEE_SALARY table

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS PAYROLL_EMPLOYEE_SALARY;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE PAYROLL_EMPLOYEE_SALARY (
  PAYROLL_EMPLOYEE_SALARY_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
  PAYROLL_ID int(10) unsigned NOT NULL,
  EMPLOYEE_ID int(10) unsigned NOT NULL,
  EB_OBJECT_ID int(10) unsigned DEFAULT NULL,
  BASIC_PAY double(12,2) NOT NULL,
  OVERTIME double(12,2) NOT NULL,
  PAID_LEAVE double(12,2) NOT NULL,
  DE_MINIMIS double(12,2) DEFAULT '0.00',
  COLA double(12,2) DEFAULT '0.00',
  BONUS double(12,2) DEFAULT '0.00',
  DEDUCTION double(12,2) DEFAULT '0.00',
  SSS double(12,2) DEFAULT '0.00',
  SSS_LOAN double(12,2) DEFAULT '0.00',
  SSS_ER double(12,2) DEFAULT '0.00',
  SSS_EC double(12,2) DEFAULT '0.00',
  PHILHEALTH double(12,2) DEFAULT '0.00',
  PHILHEALTH_ER double(12,2) DEFAULT '0.00',
  PAGIBIG double(12,2) DEFAULT '0.00',
  PAGIBIG_LOAN double(12,2) DEFAULT '0.00',
  PAGIBIG_ER double(12,2) DEFAULT '0.00',
  WITHHOLDING_TAX double(12,2) DEFAULT '0.00',
  LATE_ABSENT double(12,2) DEFAULT '0.00',
  BREAKAGE_WASTAGE DOUBLE(12,2) DEFAULT 0.00,
  CASH_ADVANCE DOUBLE(12,2) DEFAULT 0.00,
  SUNDAY_HOLIDAY_PAY DOUBLE(12,2) DEFAULT NULL,
  NIGHT_DIFFERENTIAL DOUBLE(12,2) DEFAULT NULL,
  NET_PAY double(12,2) DEFAULT NULL,
  NON_WORKING_HOLIDAY double(12,2) DEFAULT NULL,
  PRIMARY KEY (PAYROLL_EMPLOYEE_SALARY_ID),
  KEY FK_PES_EMPLOYEE_ID (EMPLOYEE_ID),
  KEY FK_PES_PAYROLL_ID (PAYROLL_ID),
  KEY FK_PES_EB_OBJECT_ID (EB_OBJECT_ID),
  CONSTRAINT FK_PES_EMPLOYEE_ID FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE (EMPLOYEE_ID),
  CONSTRAINT FK_PES_PAYROLL_ID FOREIGN KEY (PAYROLL_ID) REFERENCES PAYROLL (PAYROLL_ID),
  CONSTRAINT FK_PES_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;