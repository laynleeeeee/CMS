
-- Description	: A create script that will create SALES_ORDER table

DROP TABLE IF EXISTS SALES_ORDER;

CREATE TABLE SALES_ORDER (
	SALES_ORDER_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	SALES_QUOTATION_ID int(10) unsigned DEFAULT NULL,
	FORM_WORKFLOW_ID int(10) unsigned DEFAULT NULL,
	SEQUENCE_NO int(10) unsigned NOT NULL,
	EB_OBJECT_ID int(10) unsigned DEFAULT NULL,
	COMPANY_ID int(10) unsigned NOT NULL,
	DIVISION_ID int(10) unsigned NOT NULL,
	DATE date DEFAULT NULL,
	DELIVERY_DATE date DEFAULT NULL,
	SO_TYPE_ID int(10) unsigned NOT NULL,
	PO_NUMBER varchar(50) DEFAULT NULL,
	AR_CUSTOMER_ID int(10) unsigned NOT NULL,
	AR_CUSTOMER_ACCOUNT_ID int(10) unsigned NOT NULL,
	TERM_ID int(10) unsigned NOT NULL,
	CUSTOMER_TYPE_ID int(10) unsigned DEFAULT NULL,
	SHIP_TO text,
	REMARKS text,
	CURRENCY_ID int(10) unsigned NOT NULL,
	CURRENCY_RATE_ID int(10) unsigned DEFAULT NULL,
	CURRENCY_RATE_VALUE double DEFAULT '0',
	AMOUNT double DEFAULT '0',
	WT_ACCOUNT_SETTING_ID int(10) unsigned DEFAULT NULL,
	WT_AMOUNT double DEFAULT '0',
	WT_VAT_AMOUNT double DEFAULT '0',
	DEPOSIT tinyint(1) DEFAULT NULL,
	ADVANCE_PAYMENT double DEFAULT NULL,
	CREATED_BY int(10) unsigned NOT NULL,
	CREATED_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	UPDATED_BY int(10) unsigned NOT NULL,
	UPDATED_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
	PRIMARY KEY (SALES_ORDER_ID),
	KEY FK_SO_FORM_WORKFLOW_ID (FORM_WORKFLOW_ID),
	KEY FK_SO_SALES_QUOTATION_ID(SALES_QUOTATION_ID),
	KEY FK_SO_EB_OBJECT_ID (EB_OBJECT_ID),
	KEY FK_SO_COMPANY_ID (COMPANY_ID),
	KEY FK_SO_DIVISION_ID (DIVISION_ID),
	KEY FK_SO_SO_TYPE_ID (SO_TYPE_ID),
	KEY FK_SO_AR_CUSTOMER_ID (AR_CUSTOMER_ID),
	KEY FK_SO_AR_CUSTOMER_ACCOUNT_ID (AR_CUSTOMER_ACCOUNT_ID),
	KEY FK_SO_TERM_ID (TERM_ID),
	KEY FK_SO_CURRENCY_ID (CURRENCY_ID),
	KEY FK_SO_CURRENCY_RATE_ID (CURRENCY_RATE_ID),
	KEY FK_SO_CUSTOMER_TYPE_ID (CUSTOMER_TYPE_ID),
	KEY FK_SO_WT_ACCOUNT_SETTING_ID (WT_ACCOUNT_SETTING_ID),
	KEY FK_SO_CREATED_BY (CREATED_BY),
	KEY FK_SO_UPDATED_BY (UPDATED_BY),
	CONSTRAINT FK_SO_FORM_WORKFLOW_ID FOREIGN KEY (FORM_WORKFLOW_ID) REFERENCES FORM_WORKFLOW (FORM_WORKFLOW_ID),
	CONSTRAINT FK_SO_SALES_QUOTATION_ID FOREIGN KEY (SALES_QUOTATION_ID) REFERENCES SALES_QUOTATION (SALES_QUOTATION_ID),
	CONSTRAINT FK_SO_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID),
	CONSTRAINT FK_SO_COMPANY_ID FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY (COMPANY_ID),
	CONSTRAINT FK_SO_DIVISION_ID FOREIGN KEY (DIVISION_ID) REFERENCES DIVISION (DIVISION_ID),
	CONSTRAINT FK_SO_SO_TYPE_ID FOREIGN KEY (SO_TYPE_ID) REFERENCES SO_TYPE (SO_TYPE_ID),
	CONSTRAINT FK_SO_AR_CUSTOMER_ID FOREIGN KEY (AR_CUSTOMER_ID) REFERENCES AR_CUSTOMER (AR_CUSTOMER_ID),
	CONSTRAINT FK_SO_AR_CUSTOMER_ACCOUNT_ID FOREIGN KEY (AR_CUSTOMER_ACCOUNT_ID) REFERENCES AR_CUSTOMER_ACCOUNT (AR_CUSTOMER_ACCOUNT_ID),
	CONSTRAINT FK_SO_TERM_ID FOREIGN KEY (TERM_ID) REFERENCES TERM (TERM_ID),
	CONSTRAINT FK_SO_CURRENCY_ID FOREIGN KEY (CURRENCY_ID) REFERENCES CURRENCY (CURRENCY_ID),
	CONSTRAINT FK_SO_CURRENCY_RATE_ID FOREIGN KEY (CURRENCY_RATE_ID) REFERENCES CURRENCY_RATE (CURRENCY_RATE_ID),
	CONSTRAINT FK_SO_CUSTOMER_TYPE_ID FOREIGN KEY (CUSTOMER_TYPE_ID) REFERENCES CUSTOMER_TYPE (CUSTOMER_TYPE_ID),
	CONSTRAINT FK_SO_WT_ACCOUNT_SETTING_ID FOREIGN KEY (WT_ACCOUNT_SETTING_ID) REFERENCES WT_ACCOUNT_SETTING (WT_ACCOUNT_SETTING_ID),
	CONSTRAINT FK_SO_CREATED_BY FOREIGN KEY (CREATED_BY) REFERENCES USER (USER_ID),
	CONSTRAINT FK_SO_UPDATED_BY FOREIGN KEY (UPDATED_BY) REFERENCES USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;