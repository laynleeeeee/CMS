
-- Description: sql script in creating POS_MIDDLEWARE_SETTING table.

DROP TABLE IF EXISTS  POS_MIDDLEWARE_SETTING;

CREATE TABLE POS_MIDDLEWARE_SETTING (
	POS_MIDDLEWARE_SETTING_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	COMPANY_ID int(10) unsigned NOT NULL,
	WAREHOUSE_ID int(10) unsigned NOT NULL,
	AR_CUSTOMER_ID int(10) unsigned NOT NULL,
	AR_CUSTOMER_ACCOUNT_ID int(10) unsigned NOT NULL,
	ACTIVE tinyint(1) NOT NULL,
	CREATED_BY int(10) unsigned NOT NULL,
	CREATED_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	UPDATED_BY int(10) unsigned NOT NULL,
	UPDATED_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
	PRIMARY KEY (POS_MIDDLEWARE_SETTING_ID),
	KEY FK_POMS_COMPANY_ID (COMPANY_ID),
	KEY FK_POMS_WAREHOUSE_ID (WAREHOUSE_ID),
	KEY FK_POMS_AR_CUSTOMER_ID (AR_CUSTOMER_ID),
	KEY FK_POMS_AR_CUSTOMER_ACCOUNT_ID (AR_CUSTOMER_ACCOUNT_ID),
	KEY FK_POMS_CREATED_BY (CREATED_BY),
	KEY FK_POMS_UPDATED_BY (UPDATED_BY),
	CONSTRAINT FK_POMS_COMPANY_ID FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY (COMPANY_ID),
	CONSTRAINT FK_POMS_WAREHOUSE_ID FOREIGN KEY (WAREHOUSE_ID) REFERENCES WAREHOUSE (WAREHOUSE_ID),
	CONSTRAINT FK_POMS_AR_CUSTOMER_ID FOREIGN KEY (AR_CUSTOMER_ID) REFERENCES AR_CUSTOMER (AR_CUSTOMER_ID),
	CONSTRAINT FK_POMS_AR_CUSTOMER_ACCOUNT_ID FOREIGN KEY (AR_CUSTOMER_ACCOUNT_ID) REFERENCES AR_CUSTOMER_ACCOUNT (AR_CUSTOMER_ACCOUNT_ID),
	CONSTRAINT FK_POMS_CREATED_BY FOREIGN KEY (CREATED_BY) REFERENCES USER (USER_ID),
	CONSTRAINT FK_POMS_UPDATED_BY FOREIGN KEY (UPDATED_BY) REFERENCES USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;