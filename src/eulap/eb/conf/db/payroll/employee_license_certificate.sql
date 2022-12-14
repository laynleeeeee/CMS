
-- Description: Create script for EMPLOYEE_LICENSE_CERTIFICATE

DROP TABLE IF EXISTS EMPLOYEE_LICENSE_CERTIFICATE;

CREATE TABLE EMPLOYEE_LICENSE_CERTIFICATE (
	EMPLOYEE_LICENSE_CERTIFICATE_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	EB_OBJECT_ID int(10) unsigned DEFAULT NULL,
	ACCREDITATION_TYPE varchar(100) NOT NULL,
	DATE_ISSUED date DEFAULT NULL,
	RATING varchar(5) NOT NULL,
	ACTIVE tinyint(1) NOT NULL,
	PRIMARY KEY (EMPLOYEE_LICENSE_CERTIFICATE_ID),
	KEY FK_ELC_EB_OBJECT_ID (EB_OBJECT_ID),
	CONSTRAINT FK_ELC_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;