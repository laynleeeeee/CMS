
-- Description: Create script for LEAVE_DETAIL table.

CREATE TABLE LEAVE_DETAIL (
  LEAVE_DETAIL_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
  EMPLOYEE_REQUEST_ID int(10) unsigned NOT NULL,
  TYPE_OF_LEAVE_ID int(10) unsigned NOT NULL,
  DATE_FROM date NOT NULL,
  DATE_TO date NOT NULL,
  LEAVE_DAYS double(4,2) NOT NULL,
  REMARKS text,
  HALF_DAY tinyint(1) unsigned DEFAULT '0',
  PERIOD int(11) unsigned DEFAULT '0',
  PRIMARY KEY (LEAVE_DETAIL_ID),
  KEY FK_LD_EMPLOYEE_REQUEST_ID (EMPLOYEE_REQUEST_ID),
  KEY FK_LD_TYPE_OF_LEAVE_ID (TYPE_OF_LEAVE_ID),
  CONSTRAINT FK_LD_EMPLOYEE_REQUEST_ID FOREIGN KEY (EMPLOYEE_REQUEST_ID) REFERENCES EMPLOYEE_REQUEST (EMPLOYEE_REQUEST_ID),
  CONSTRAINT FK_LD_TYPE_OF_LEAVE_ID FOREIGN KEY (TYPE_OF_LEAVE_ID) REFERENCES TYPE_OF_LEAVE (TYPE_OF_LEAVE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;