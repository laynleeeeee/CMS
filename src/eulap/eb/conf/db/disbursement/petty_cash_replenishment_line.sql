
-- Description: Sql script for creating PETTY_CASH_REPLENISHMENT_LINE table

DROP TABLE IF EXISTS PETTY_CASH_REPLENISHMENT_LINE;

CREATE TABLE PETTY_CASH_REPLENISHMENT_LINE (
	PETTY_CASH_REPLENISHMENT_LINE_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	AP_INVOICE_ID int(10) unsigned NOT NULL,
	ACCOUNT_COMBINATION_ID int(10) unsigned NOT NULL,
	EB_OBJECT_ID int(10) unsigned DEFAULT NULL,
	PRIMARY KEY (PETTY_CASH_REPLENISHMENT_LINE_ID),
	KEY FK_PCR_AP_INVOICE_ID (AP_INVOICE_ID),
	KEY FK_PCR_ACCOUNT_COMBINATION_ID (ACCOUNT_COMBINATION_ID),
	KEY FK_PCR_EB_OBJECT_ID (EB_OBJECT_ID),
	CONSTRAINT FK_PCR_AP_INVOICE_ID FOREIGN KEY (AP_INVOICE_ID) REFERENCES AP_INVOICE (AP_INVOICE_ID),
	CONSTRAINT FK_PCR_ACCOUNT_COMBINATION_ID FOREIGN KEY (ACCOUNT_COMBINATION_ID) REFERENCES ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID),
	CONSTRAINT FK_PCR_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;