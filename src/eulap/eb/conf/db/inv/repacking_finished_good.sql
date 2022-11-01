
-- Description	: SQL script in creating REPACKING_FINISHED_GOOD table.

DROP TABLE IF EXISTS REPACKING_FINISHED_GOOD;

CREATE TABLE REPACKING_FINISHED_GOOD (
	REPACKING_FINISHED_GOOD_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	EB_OBJECT_ID int(10) unsigned DEFAULT NULL,
	REPACKING_ID int(10) unsigned NOT NULL,
	ITEM_ID int(10) unsigned NOT NULL,
	QUANTITY double NOT NULL,
	UNIT_COST double DEFAULT '0',
	PRIMARY KEY (REPACKING_FINISHED_GOOD_ID),
	KEY FK_RFG_EB_OBJECT_ID (EB_OBJECT_ID),
	KEY FK_RFG_REPACKING_ID (REPACKING_ID),
	KEY FK_RFG_ITEM_ID (ITEM_ID),
	CONSTRAINT FK_RFG_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID),
	CONSTRAINT FK_RFG_REPACKING_ID FOREIGN KEY (REPACKING_ID) REFERENCES REPACKING (REPACKING_ID),
	CONSTRAINT FK_RFG_ITEM_ID FOREIGN KEY (ITEM_ID) REFERENCES ITEM (ITEM_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;