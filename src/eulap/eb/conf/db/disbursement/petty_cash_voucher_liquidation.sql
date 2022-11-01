
-- Description: Sql script for creating PETTY_CASH_VOUCHER_LIQUIDATION table

DROP TABLE IF EXISTS PETTY_CASH_VOUCHER_LIQUIDATION;

CREATE TABLE PETTY_CASH_VOUCHER_LIQUIDATION (
	PETTY_CASH_VOUCHER_LIQUIDATION_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	FORM_WORKFLOW_ID int(10) unsigned DEFAULT NULL,
	EB_OBJECT_ID int(10) unsigned DEFAULT NULL,
	SEQUENCE_NO int(10) unsigned NOT NULL,
	COMPANY_ID int(10) unsigned NOT NULL,
	DIVISION_ID int(10) unsigned NOT NULL,
	PETTY_CASH_VOUCHER_ID int(10) unsigned NOT NULL,
	USER_CUSTODIAN_ID int(10) unsigned NOT NULL,
	PCVL_DATE date NOT NULL,
	REQUESTOR text,
	REFERENCE_NO text,
	DESCRIPTION text,
	AMOUNT double DEFAULT '0',
	CASH_RETURNED double DEFAULT '0',
	CREATED_BY int(10) unsigned NOT NULL,
	CREATED_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	UPDATED_BY int(10) unsigned NOT NULL,
	UPDATED_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
	PRIMARY KEY (PETTY_CASH_VOUCHER_LIQUIDATION_ID),
	KEY FK_PETTY_CASH_VOUCHER_LIQUIDATION_EB_OBJECT_ID (EB_OBJECT_ID),
	KEY FK_PETTY_CASH_VOUCHER_LIQUIDATION_COMPANY_ID (COMPANY_ID),
	KEY FK_PETTY_CASH_VOUCHER_LIQUIDATION_DIVISION_ID (DIVISION_ID),
	KEY FK_PETTY_CASH_VOUCHER_LIQUIDATION_PETTY_CASH_VOUCHER_ID (PETTY_CASH_VOUCHER_ID),
	KEY FK_PETTY_CASH_VOUCHER_LIQUIDATION_USER_CUSTODIAN_ID (USER_CUSTODIAN_ID),
	KEY FK_PETTY_CASH_VOUCHER_LIQUIDATION_CREATED_BY (CREATED_BY),
	KEY FK_PETTY_CASH_VOUCHER_LIQUIDATION_UPDATED_BY (UPDATED_BY),
	KEY FK_PETTY_CASH_VOUCHER_LIQUIDATION_FORM_WORKFLOW_ID (FORM_WORKFLOW_ID),
	CONSTRAINT FK_PETTY_CASH_VOUCHER_LIQUIDATION_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID),
	CONSTRAINT FK_PETTY_CASH_VOUCHER_LIQUIDATION_COMPANY_ID FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY (COMPANY_ID),
	CONSTRAINT FK_PETTY_CASH_VOUCHER_LIQUIDATION_DIVISION_ID FOREIGN KEY (DIVISION_ID) REFERENCES DIVISION (DIVISION_ID),
	CONSTRAINT FK_PETTY_CASH_VOUCHER_LIQUIDATION_PETTY_CASH_VOUCHER_ID FOREIGN KEY (PETTY_CASH_VOUCHER_ID) REFERENCES PETTY_CASH_VOUCHER (PETTY_CASH_VOUCHER_ID),
	CONSTRAINT FK_PETTY_CASH_VOUCHER_LIQUIDATION_USER_CUSTODIAN_ID FOREIGN KEY (USER_CUSTODIAN_ID) REFERENCES USER_CUSTODIAN (USER_CUSTODIAN_ID),
	CONSTRAINT FK_PETTY_CASH_VOUCHER_LIQUIDATION_FORM_WORKFLOW_ID FOREIGN KEY (FORM_WORKFLOW_ID) REFERENCES FORM_WORKFLOW (FORM_WORKFLOW_ID),
	CONSTRAINT FK_PETTY_CASH_VOUCHER_LIQUIDATION_CREATED_BY FOREIGN KEY (CREATED_BY) REFERENCES USER (USER_ID),
	CONSTRAINT FK_PETTY_CASH_VOUCHER_LIQUIDATION_UPDATED_BY FOREIGN KEY (UPDATED_BY) REFERENCES USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;