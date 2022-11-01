
-- Description: sql script in creating DIRECT_PAYMENT_LINE table.

DROP TABLE IF EXISTS DIRECT_PAYMENT_LINE;

CREATE TABLE DIRECT_PAYMENT_LINE (
	DIRECT_PAYMENT_LINE_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	DIRECT_PAYMENT_ID int(10) unsigned NOT NULL,
	ACCOUNT_COMBINATION_ID int(10) unsigned NOT NULL,
	AMOUNT double NOT NULL,
	DESCRIPTION text,
	ACTIVE tinyint(1) NOT NULL,
	PRIMARY KEY (DIRECT_PAYMENT_LINE_ID),
	KEY FK_DIRECT_PAYMENT_LINE_DIRECT_PAYMENT_ID (DIRECT_PAYMENT_ID),
	KEY FK_DIRECT_PAYMENT_LINE_ACCOUNT_COMBINATION_ID (ACCOUNT_COMBINATION_ID),
	CONSTRAINT FK_DIRECT_PAYMENT_LINE_DIRECT_PAYMENT_ID FOREIGN KEY (DIRECT_PAYMENT_ID)
	REFERENCES DIRECT_PAYMENT (DIRECT_PAYMENT_ID),
	CONSTRAINT FK_DIRECT_PAYMENT_LINE_ACCOUNT_COMBINATION FOREIGN KEY (ACCOUNT_COMBINATION_ID)
	REFERENCES ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;