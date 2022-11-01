
-- Description: Create script for LEAVES Table.

DROP TABLE IF EXISTS ELC_LINE;

CREATE TABLE ELC_LINE(
ELC_LINE_ID INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
EB_OBJECT_ID INT(10) UNSIGNED DEFAULT NULL,
EMPLOYEE_ID INT(10) UNSIGNED NOT NULL,
AVAILABLE_LEAVES DOUBLE(4,2) UNSIGNED,
DEDUCT_DEBIT DOUBLE(4,2) UNSIGNED,
ADD_CREDIT DOUBLE(4,2) UNSIGNED,
PRIMARY KEY (ELC_LINE_ID),
KEY FK_ELC_LINE_EB_OBJECT_ID (EB_OBJECT_ID),
KEY FK_ELC_LINE_EMPLOYEE_ID (EMPLOYEE_ID),
CONSTRAINT FK_ELC_LINE_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID),
CONSTRAINT FK_ELC_LINE_EMPLOYEE_ID FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE (EMPLOYEE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;