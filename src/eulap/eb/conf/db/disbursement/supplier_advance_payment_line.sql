
-- Description	: Create script for SUPPLIER_ADVANCE_PAYMENT_LINE table.

DROP TABLE IF EXISTS SUPPLIER_ADVANCE_PAYMENT_LINE;

CREATE TABLE SUPPLIER_ADVANCE_PAYMENT_LINE (
	SUPPLIER_ADVANCE_PAYMENT_LINE_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	SUPPLIER_ADVANCE_PAYMENT_ID int(10) unsigned NOT NULL,
	EB_OBJECT_ID int(10) unsigned DEFAULT NULL,
	AMOUNT double DEFAULT 0,
	PRIMARY KEY (SUPPLIER_ADVANCE_PAYMENT_LINE_ID),
	KEY FK_SAPL_SAP_ID (SUPPLIER_ADVANCE_PAYMENT_ID),
	KEY FK_SAPL_EB_OBJECT_ID (EB_OBJECT_ID),
	CONSTRAINT FK_SAPL_SAP_ID FOREIGN KEY (SUPPLIER_ADVANCE_PAYMENT_ID) REFERENCES SUPPLIER_ADVANCE_PAYMENT (SUPPLIER_ADVANCE_PAYMENT_ID),
	CONSTRAINT FK_SAPL_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;