
-- Description: Create script for FORM_DEDUCTION_LINE Table.

DROP TABLE IF EXISTS FORM_DEDUCTION_LINE;

CREATE TABLE FORM_DEDUCTION_LINE(
FORM_DEDUCTION_LINE_ID INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
EB_OBJECT_ID INT(10) UNSIGNED NOT NULL,
DATE DATE NOT NULL,
AMOUNT DOUBLE NOT NULL,
PRIMARY KEY (FORM_DEDUCTION_LINE_ID),
KEY FK_FORM_DEDUCTION_LINE_EB_OBJECT_ID (EB_OBJECT_ID),
CONSTRAINT FK_FORM_DEDUCTION_LINE_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;