
-- Description	: Create script for AR_INVOICE_TRANSACTION

DROP TABLE IF EXISTS AR_INVOICE_TRANSACTION;

CREATE TABLE AR_INVOICE_TRANSACTION (
	AR_INVOICE_TRANSACTION_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	AR_INVOICE_ID int(10) unsigned NOT NULL,
	AR_TRANSACTION_ID int(10) unsigned NOT NULL,
	PRIMARY KEY (AR_INVOICE_TRANSACTION_ID),
	KEY FK_ARI_AR_INVOICE_ID (AR_INVOICE_ID),
	KEY FK_ARI_AR_TRANSACTION_ID (AR_TRANSACTION_ID),
	CONSTRAINT FK_ARI_AR_INVOICE_ID FOREIGN KEY (AR_INVOICE_ID) REFERENCES AR_INVOICE (AR_INVOICE_ID),
	CONSTRAINT FK_ARI_AR_TRANSACTION_ID FOREIGN KEY (AR_TRANSACTION_ID) REFERENCES AR_TRANSACTION (AR_TRANSACTION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;