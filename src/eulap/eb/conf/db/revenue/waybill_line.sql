
-- Description	: A create script that will create WAYBILL_LINE table

DROP TABLE IF EXISTS WAYBILL_LINE;

CREATE TABLE WAYBILL_LINE (
	WAYBILL_LINE_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	DELIVERY_RECEIPT_ID int(10) unsigned NOT NULL,
	EB_OBJECT_ID int(10) unsigned DEFAULT NULL,
	AR_LINE_SETUP_ID int(10) unsigned NOT NULL,
	DISCOUNT_TYPE_ID int(10) unsigned DEFAULT NULL,
	DISCOUNT_VALUE double DEFAULT '0',
	DISCOUNT double DEFAULT '0',
	TAX_TYPE_ID int(10) unsigned DEFAULT NULL,
	VAT_AMOUNT double DEFAULT '0',
	AMOUNT double DEFAULT '0',
	QUANTITY double DEFAULT '0',
	UP_AMOUNT double DEFAULT '0',
	UNITOFMEASUREMENT_ID int(10) unsigned DEFAULT NULL,
	PRIMARY KEY (WAYBILL_LINE_ID),
	KEY FK_WAYBILL_LINE_DELIVERY_RECEIPT_ID (DELIVERY_RECEIPT_ID),
	KEY FK_WAYBILL_LINE_EB_OBJECT_ID (EB_OBJECT_ID),
	KEY FK_WAYBILL_LINE_AR_LINE_SETUP_ID (AR_LINE_SETUP_ID),
	KEY FK_WAYBILL_LINE_DISCOUNT_TYPE_ID (DISCOUNT_TYPE_ID),
	KEY FK_WAYBILL_LINE_TAX_TYPE_ID (TAX_TYPE_ID),
	CONSTRAINT FK_WAYBILL_LINE_DELIVERY_RECEIPT_ID FOREIGN KEY (DELIVERY_RECEIPT_ID) REFERENCES DELIVERY_RECEIPT (DELIVERY_RECEIPT_ID),
	CONSTRAINT FK_WAYBILL_LINE_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID),
	CONSTRAINT FK_WAYBILL_LINE_DISCOUNT_TYPE_ID FOREIGN KEY (DISCOUNT_TYPE_ID) REFERENCES ITEM_DISCOUNT_TYPE (ITEM_DISCOUNT_TYPE_ID),
	CONSTRAINT FK_WAYBILL_LINE_AR_LINE_SETUP_ID FOREIGN KEY (AR_LINE_SETUP_ID) REFERENCES AR_LINE_SETUP (AR_LINE_SETUP_ID),
	CONSTRAINT FK_WAYBILL_LINE_TAX_TYPE_ID FOREIGN KEY (TAX_TYPE_ID) REFERENCES TAX_TYPE (TAX_TYPE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;