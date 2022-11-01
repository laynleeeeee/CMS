
-- Description: Create script for EMPLOYEE_EMERGENCY_CONTACT

DROP TABLE IF EXISTS EMPLOYEE_EMERGENCY_CONTACT;

CREATE TABLE EMPLOYEE_EMERGENCY_CONTACT (
	EMPLOYEE_EMERGENCY_CONTACT_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	EB_OBJECT_ID int(10) unsigned DEFAULT NULL,
	NAME varchar(100) NOT NULL,
	ADDRESS varchar(150) NOT NULL,
	CONTACT_NO varchar(20) NOT NULL,
	ACTIVE tinyint(1) NOT NULL,
	PRIMARY KEY (EMPLOYEE_EMERGENCY_CONTACT_ID),
	KEY FK_EEC_EB_OBJECT_ID (EB_OBJECT_ID),
	CONSTRAINT FK_EEC_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;