
-- Description: Create script for SERIAL_ITEM_SETUP
DROP TABLE IF EXISTS SERIAL_ITEM_SETUP;

CREATE TABLE SERIAL_ITEM_SETUP (
	SERIAL_ITEM_SETUP_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	ITEM_ID int(10) unsigned NOT NULL,
	SERIALIZED_ITEM tinyint(1) DEFAULT 0,
	PRIMARY KEY (SERIAL_ITEM_SETUP_ID),
	KEY FK_SIS_ITEM_ID (ITEM_ID),
	CONSTRAINT FK_SIS_ITEM_ID FOREIGN KEY (ITEM_ID) REFERENCES ITEM (ITEM_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;