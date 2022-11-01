
-- Description	: Create script for AR_INVOICE

DROP TABLE IF EXISTS AR_INVOICE;

CREATE TABLE AR_INVOICE (
	AR_INVOICE_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	DELIVERY_RECEIPT_ID int(10) unsigned NOT NULL,
	SEQUENCE_NO int(10) unsigned NOT NULL,
	FORM_WORKFLOW_ID int(10) unsigned NOT NULL,
	COMPANY_ID int(10) unsigned NOT NULL,
	AR_CUSTOMER_ID int(10) unsigned NOT NULL,
	AR_CUSTOMER_ACCOUNT_ID int(10) unsigned NOT NULL,
	TERM_ID int(10) unsigned NOT NULL,
	EB_OBJECT_ID int(10) unsigned DEFAULT NULL,
	DIVISION_ID INT(10) UNSIGNED DEFAULT NULL,
	CURRENCY_ID INT(10) UNSIGNED DEFAULT NULL,
	CURRENCY_RATE_ID INT(10) UNSIGNED DEFAULT NULL,
	CURRENCY_RATE_VALUE double DEFAULT 0,
	DATE date NOT NULL,
	DUE_DATE date NOT NULL,
	REMARKS text DEFAULT NULL,
	WT_ACCOUNT_SETTING_ID int(10) unsigned DEFAULT NULL,
	WT_AMOUNT double DEFAULT '0',
	WT_VAT_AMOUNT double DEFAULT '0',
	AMOUNT double DEFAULT '0',
	RETENTION double DEFAULT '0',
	RECOUPMENT double DEFAULT '0',
	CREATED_BY INT(10) UNSIGNED NOT NULL,
	CREATED_DATE TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	UPDATED_BY INT(10) UNSIGNED NOT NULL,
	UPDATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
	PRIMARY KEY (AR_INVOICE_ID),
	KEY FK_ARI_DELIVERY_RECEIPT_ID (DELIVERY_RECEIPT_ID),
	KEY FK_ARI_FORM_WORKFLOW_ID (FORM_WORKFLOW_ID),
	KEY FK_ARI_COMPANY_ID (COMPANY_ID),
	KEY FK_ARI_CS_ID (AR_CUSTOMER_ID),
	KEY FK_ARI_CS_ACCT_ID (AR_CUSTOMER_ACCOUNT_ID),
	KEY FK_ARI_TERM_ID (TERM_ID),
	KEY FK_ARI_EB_OBJECT_ID (EB_OBJECT_ID),
	KEY FK_ARI_CREATED_BY (CREATED_BY),
	KEY FK_ARI_UPDATED_BY (UPDATED_BY),
	KEY FK_ARI_DIVISION_ID (DIVISION_ID),
	KEY FK_ARI_CURRENCY_ID (CURRENCY_ID),
	KEY FK_ARI_CURRENCY_RATE_ID (CURRENCY_RATE_ID),
	CONSTRAINT FK_ARI_DELIVERY_RECEIPT_ID FOREIGN KEY (DELIVERY_RECEIPT_ID) REFERENCES DELIVERY_RECEIPT (DELIVERY_RECEIPT_ID),
	CONSTRAINT FK_ARI_FORM_WORKFLOW_ID FOREIGN KEY (FORM_WORKFLOW_ID) REFERENCES FORM_WORKFLOW (FORM_WORKFLOW_ID),
	CONSTRAINT FK_ARI_COMPANY_ID FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY (COMPANY_ID),
	CONSTRAINT FK_ARI_CS_ID FOREIGN KEY (AR_CUSTOMER_ID) REFERENCES AR_CUSTOMER (AR_CUSTOMER_ID),
	CONSTRAINT FK_ARI_CS_ACCT_ID FOREIGN KEY (AR_CUSTOMER_ACCOUNT_ID) REFERENCES AR_CUSTOMER_ACCOUNT (AR_CUSTOMER_ACCOUNT_ID),
	CONSTRAINT FK_ARI_TERM_ID FOREIGN KEY (TERM_ID) REFERENCES TERM (TERM_ID),
	CONSTRAINT FK_ARI_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID),
	CONSTRAINT FK_ARI_CREATED_BY FOREIGN KEY (CREATED_BY) REFERENCES USER (USER_ID),
	CONSTRAINT FK_ARI_UPDATED_BY FOREIGN KEY (UPDATED_BY) REFERENCES USER (USER_ID),
	CONSTRAINT FK_ARI_DIVISION_ID FOREIGN KEY (DIVISION_ID) REFERENCES DIVISION (DIVISION_ID),
	CONSTRAINT FK_ARI_CURRENCY_ID FOREIGN KEY (CURRENCY_ID) REFERENCES CURRENCY (CURRENCY_ID),
	CONSTRAINT FK_ARI_CURRENCY_RATE_ID FOREIGN KEY (CURRENCY_RATE_ID) REFERENCES CURRENCY_RATE (CURRENCY_RATE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;