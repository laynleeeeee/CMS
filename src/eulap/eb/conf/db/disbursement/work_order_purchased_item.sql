
-- Description	: A create script that will create WORK_ORDER_PURCHASED_ITEM table.

DROP TABLE IF EXISTS WORK_ORDER_PURCHASED_ITEM;

CREATE TABLE WORK_ORDER_PURCHASED_ITEM (
	WORK_ORDER_PURCHASED_ITEM_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	WORK_ORDER_ID int(10) unsigned NOT NULL,
	EB_OBJECT_ID int(10) unsigned DEFAULT NULL,
	WAREHOUSE_ID int(10) unsigned DEFAULT NULL,
	ITEM_ID int(10) unsigned NOT NULL,
	QUANTITY double DEFAULT 0,
	PRIMARY KEY (WORK_ORDER_PURCHASED_ITEM_ID),
	KEY FK_WOPI_WORK_ORDER_ID (WORK_ORDER_ID),
	KEY FK_WOPI_EB_OBJECT_ID (EB_OBJECT_ID),
	KEY FK_WOPI_WAREHOUSE_ID (WAREHOUSE_ID),
	KEY FK_WOPI_ITEM_ID (ITEM_ID),
	CONSTRAINT FK_WOPI_WORK_ORDER_ID FOREIGN KEY (WORK_ORDER_ID) REFERENCES WORK_ORDER (WORK_ORDER_ID),
	CONSTRAINT FK_WOPI_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID),
	CONSTRAINT FK_WOPI_WAREHOUSE_ID FOREIGN KEY (WAREHOUSE_ID) REFERENCES WAREHOUSE (WAREHOUSE_ID),
	CONSTRAINT FK_WOPI_ITEM_ID FOREIGN KEY (ITEM_ID) REFERENCES ITEM (ITEM_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;