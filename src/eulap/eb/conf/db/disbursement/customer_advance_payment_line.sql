
-- Description	: Create script for CUSTOMER_ADVANCE_PAYMENT_LINE table.

DROP TABLE IF EXISTS CUSTOMER_ADVANCE_PAYMENT_LINE;

CREATE TABLE CUSTOMER_ADVANCE_PAYMENT_LINE (
	CUSTOMER_ADVANCE_PAYMENT_LINE_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	CUSTOMER_ADVANCE_PAYMENT_ID int(10) unsigned NOT NULL,
	EB_OBJECT_ID int(10) unsigned DEFAULT NULL,
	AMOUNT double DEFAULT 0,
	PRIMARY KEY (CUSTOMER_ADVANCE_PAYMENT_LINE_ID),
	KEY FK_CAPL_CAP_ID (CUSTOMER_ADVANCE_PAYMENT_ID),
	KEY FK_CAPL_EB_OBJECT_ID (EB_OBJECT_ID),
	CONSTRAINT FK_CAPL_CAP_ID FOREIGN KEY (CUSTOMER_ADVANCE_PAYMENT_ID) REFERENCES CUSTOMER_ADVANCE_PAYMENT (CUSTOMER_ADVANCE_PAYMENT_ID),
	CONSTRAINT FK_CAPL_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;