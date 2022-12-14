
-- Description	: A create script that will create SALES_QUOTATION table

DROP TABLE IF EXISTS SALES_QUOTATION;

CREATE TABLE SALES_QUOTATION (
	SALES_QUOTATION_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	FORM_WORKFLOW_ID int(10) unsigned DEFAULT NULL,
	SEQUENCE_NO int(10) unsigned NOT NULL,
	EB_OBJECT_ID int(10) unsigned DEFAULT NULL,
	COMPANY_ID int(10) unsigned NOT NULL,
	AR_CUSTOMER_ID int(10) unsigned NOT NULL,
	AR_CUSTOMER_ACCOUNT_ID int(10) unsigned NOT NULL,
	CUSTOMER_TYPE_ID int(10) unsigned DEFAULT NULL,
	DATE date DEFAULT NULL,
	SHIP_TO text,
	SUBJECT text,
	GENERAL_CONDITIONS text,
	AMOUNT double DEFAULT '0',
	WT_ACCOUNT_SETTING_ID int(10) unsigned DEFAULT NULL,
	WT_AMOUNT double DEFAULT '0',
	CREATED_BY int(10) unsigned NOT NULL,
	CREATED_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	UPDATED_BY int(10) unsigned NOT NULL,
	UPDATED_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
	PRIMARY KEY (SALES_QUOTATION_ID),
	KEY FK_SQ_FORM_WORKFLOW_ID (FORM_WORKFLOW_ID),
	KEY FK_SQ_EB_OBJECT_ID (EB_OBJECT_ID),
	KEY FK_SQ_COMPANY_ID (COMPANY_ID),
	KEY FK_SQ_AR_CUSTOMER_ID (AR_CUSTOMER_ID),
	KEY FK_SQ_AR_CUSTOMER_ACCOUNT_ID (AR_CUSTOMER_ACCOUNT_ID),
	KEY FK_SQ_CUSTOMER_TYPE_ID (CUSTOMER_TYPE_ID),
	KEY FK_SQ_WT_ACCOUNT_SETTING_ID (WT_ACCOUNT_SETTING_ID),
	KEY FK_SQ_CREATED_BY (CREATED_BY),
	KEY FK_SQ_UPDATED_BY (UPDATED_BY),
	CONSTRAINT FK_SQ_FORM_WORKFLOW_ID FOREIGN KEY (FORM_WORKFLOW_ID) REFERENCES FORM_WORKFLOW (FORM_WORKFLOW_ID),
	CONSTRAINT FK_SQ_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID),
	CONSTRAINT FK_SQ_COMPANY_ID FOREIGN KEY (AR_CUSTOMER_ID) REFERENCES AR_CUSTOMER (AR_CUSTOMER_ID),
	CONSTRAINT FK_SQ_AR_CUSTOMER_ID FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY (COMPANY_ID),
	CONSTRAINT FK_SQ_AR_CUSTOMER_ACCOUNT_ID FOREIGN KEY (AR_CUSTOMER_ACCOUNT_ID) REFERENCES AR_CUSTOMER_ACCOUNT (AR_CUSTOMER_ACCOUNT_ID),
	CONSTRAINT FK_SQ_CUSTOMER_TYPE_ID FOREIGN KEY (CUSTOMER_TYPE_ID) REFERENCES CUSTOMER_TYPE (CUSTOMER_TYPE_ID),
	CONSTRAINT FK_SQ_WT_ACCOUNT_SETTING_ID FOREIGN KEY (WT_ACCOUNT_SETTING_ID) REFERENCES WT_ACCOUNT_SETTING (WT_ACCOUNT_SETTING_ID),
	CONSTRAINT FK_SQ_CREATED_BY FOREIGN KEY (CREATED_BY) REFERENCES USER (USER_ID),
	CONSTRAINT FK_SQ_UPDATED_BY FOREIGN KEY (UPDATED_BY) REFERENCES USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;