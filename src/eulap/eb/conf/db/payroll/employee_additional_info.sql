
-- Description: Create script for EMPLOYEE_ADDITIONAL_INFO Table.

CREATE TABLE EMPLOYEE_ADDITIONAL_INFO(
	EMPLOYEE_ADDITIONAL_INFO_ID INT(10) unsigned NOT NULL AUTO_INCREMENT,
	EMPLOYEE_ID INT(10) unsigned NOT NULL,
	BLOOD_TYPE varchar(15) NOT NULL,
	TIN varchar(15) DEFAULT NULL,
	PHILHEALTH_NO varchar(15) DEFAULT NULL,
	SSS_NO varchar(15) DEFAULT NULL,
	HDMF_NO varchar(15) DEFAULT NULL,
	HIRED_DATE date  NOT NULL,
	EMPLOYMENT_PERIOD_FROM date  DEFAULT NULL,
	EMPLOYMENT_PERIOD_TO date  DEFAULT NULL,
	PERMANENT_ADDRESS varchar(15) DEFAULT NULL,
	CITIZENSHIP varchar(25) NOT NULL,
	HEIGHT varchar(10) DEFAULT NULL,
	WEIGHT varchar(10) DEFAULT NULL,
	EYE_COLOR varchar(15) DEFAULT NULL,
	HAIR_COLOR varchar(15) DEFAULT NULL,
	TELEPHONE_NUMBER varchar(15) DEFAULT NULL,
	SKILL_TALENT varchar(250) DEFAULT NULL,
	LANGUAGE_DIALECT varchar(250) DEFAULT NULL,
	EB_OBJECT_ID INT(10) unsigned NOT NULL,
	CREATED_BY INT(10) unsigned NOT NULL,
	CREATED_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	UPDATED_BY INT(10) unsigned NOT NULL,
	UPDATED_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
	PRIMARY KEY (EMPLOYEE_ADDITIONAL_INFO_ID),
	KEY FK_EMPLOYEE_ADDITIONAL_INFO_EMPLOYEE_ID (EMPLOYEE_ID),
	KEY FK_EMPLOYEE_ADDITIONAL_INFO_EB_OBJECT_ID (EB_OBJECT_ID),
	KEY FK_EMPLOYEE_ADDITIONAL_INFO_CREATED_BY (CREATED_BY),
	KEY FK_EMPLOYEE_ADDITIONAL_INFO_UPDATED_BY (UPDATED_BY),
	CONSTRAINT FK_EMPLOYEE_ADDIATIONAL_INFO_EMPLOYEE_ID FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE (EMPLOYEE_ID),
	CONSTRAINT FK_EMPLOYEE_ADDITIONAL_INFO_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID),
	CONSTRAINT FK_EMPLOYEE_ADDITIONAL_INFO_CREATED_BY FOREIGN KEY (CREATED_BY) REFERENCES USER (USER_ID),
	CONSTRAINT FK_EMPLOYEE_ADDITIONAL_INFO_UPDATED_BY FOREIGN KEY (UPDATED_BY) REFERENCES USER (USER_ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;