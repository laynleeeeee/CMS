
-- Description	: Create script for AR_RECEIPT_LINE table.

DROP TABLE IF EXISTS AR_RECEIPT_LINE;

CREATE TABLE AR_RECEIPT_LINE (
	AR_RECEIPT_LINE_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	AR_RECEIPT_ID int(10) unsigned NOT NULL,
	AR_RECEIPT_LINE_TYPE_ID int(10) unsigned NOT NULL,
	EB_OBJECT_ID int(10) unsigned DEFAULT NULL,
	AMOUNT double DEFAULT 0,
	CURRENCY_RATE_VALUE double DEFAULT 0,
	PRIMARY KEY (AR_RECEIPT_LINE_ID),
	KEY FK_ARL_AR_RECEIPT_ID (AR_RECEIPT_ID),
	KEY FK_ARL_EB_OBJECT_ID (EB_OBJECT_ID),
	KEY FK_ARL_AR_RECEIPT_LINE_TYPE_ID (AR_RECEIPT_LINE_TYPE_ID),
	CONSTRAINT FK_ARL_AR_RECEIPT_ID FOREIGN KEY (AR_RECEIPT_ID) REFERENCES AR_RECEIPT (AR_RECEIPT_ID),
	CONSTRAINT FK_ARL_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID),
	CONSTRAINT FK_ARL_AR_RECEIPT_LINE_TYPE_ID FOREIGN KEY (AR_RECEIPT_LINE_TYPE_ID) REFERENCES AR_RECEIPT_LINE_TYPE (AR_RECEIPT_LINE_TYPE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;