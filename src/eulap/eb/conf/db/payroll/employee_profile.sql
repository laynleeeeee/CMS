
-- Description: Create script for EMPLOYEE_PROFILE Table.

DROP TABLE IF EXISTS EMPLOYEE_PROFILE;

CREATE TABLE EMPLOYEE_PROFILE (
	EMPLOYEE_PROFILE_ID INT(10) unsigned NOT NULL AUTO_INCREMENT,
	EMPLOYEE_ID INT(10) unsigned NOT NULL,
	EMPLOYEE_NUMBER INT(10) unsigned NOT NULL,
	RFID varchar(10) DEFAULT NULL,
	BLOOD_TYPE varchar(15) DEFAULT NULL,
	TIN varchar(15) DEFAULT NULL,
	PHILHEALTH_NO varchar(15) DEFAULT NULL,
	SSS_NO varchar(15) DEFAULT NULL,
	HDMF_NO varchar(15) DEFAULT NULL,
	HIRED_DATE date  NOT NULL,
	EMPLOYMENT_PERIOD_FROM date  DEFAULT NULL,
	EMPLOYMENT_PERIOD_TO date  DEFAULT NULL,
	PERMANENT_ADDRESS varchar(150) DEFAULT NULL,
	BIRTH_PLACE varchar(150) DEFAULT NULL,
	CITIZENSHIP varchar(25) DEFAULT NULL,
	HEIGHT varchar(10) DEFAULT NULL,
	WEIGHT varchar(10) DEFAULT NULL,
	EYE_COLOR varchar(15) DEFAULT NULL,
	HAIR_COLOR varchar(15) DEFAULT NULL,
	TELEPHONE_NUMBER varchar(15) DEFAULT NULL,
	GENDER_ID INT(10) unsigned NOT NULL,
	CIVIL_STATUS_ID INT(10) unsigned NOT NULL,
	RELIGION varchar(25) DEFAULT NULL,
	LANGUAGE_DIALECT varchar(250) DEFAULT NULL,
	EB_OBJECT_ID INT(10) unsigned NOT NULL,
	EMPLOYEE_SHIFT_ID INT(10) UNSIGNED,
	CREATED_BY INT(10) unsigned NOT NULL,
	CREATED_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	UPDATED_BY INT(10) unsigned NOT NULL,
	UPDATED_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
	PRIMARY KEY (EMPLOYEE_PROFILE_ID),
	KEY FK_EMPLOYEE_PROFILE_EMPLOYEE_ID (EMPLOYEE_ID),
	KEY FK_EMPLOYEE_PROFILE_GENDER_ID (GENDER_ID),	
	KEY FK_EMPLOYEE_PROFILE_CIVIL_STATUS_ID (CIVIL_STATUS_ID),	
	KEY FK_EMPLOYEE_PROFILE_EB_OBJECT_ID (EB_OBJECT_ID),
	KEY FK_EMPLOYEE_PROFILE_CREATED_BY (CREATED_BY),
	KEY FK_EMPLOYEE_PROFILE_UPDATED_BY (UPDATED_BY),
	KEY FK_EMPLOYEE_PROFILE_EMPLOYEE_SHIFT_ID (EMPLOYEE_SHIFT_ID),
	CONSTRAINT FK_EMPLOYEE_PROFILE_EMPLOYEE_ID FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE (EMPLOYEE_ID),
	CONSTRAINT FK_EMPLOYEE_PROFILE_GENDER_ID FOREIGN KEY (GENDER_ID) REFERENCES GENDER (GENDER_ID),
	CONSTRAINT FK_EMPLOYEE_PROFILE_CIVIL_STATUS_ID FOREIGN KEY (CIVIL_STATUS_ID) REFERENCES CIVIL_STATUS (CIVIL_STATUS_ID),
	CONSTRAINT FK_EMPLOYEE_PROFILE_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID),
	CONSTRAINT FK_EMPLOYEE_PROFILE_CREATED_BY FOREIGN KEY (CREATED_BY) REFERENCES USER (USER_ID),
	CONSTRAINT FK_EMPLOYEE_PROFILE_UPDATED_BY FOREIGN KEY (UPDATED_BY) REFERENCES USER (USER_ID),
	CONSTRAINT FK_EMPLOYEE_PROFILE_EMPLOYEE_SHIFT_ID FOREIGN KEY (EMPLOYEE_SHIFT_ID) REFERENCES EMPLOYEE_SHIFT (EMPLOYEE_SHIFT_ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;