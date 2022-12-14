
-- Description: Create script for ITEM_VAT_TYPE table.

DROP TABLE IF EXISTS ITEM_VAT_TYPE;

CREATE TABLE ITEM_VAT_TYPE(
ITEM_VAT_TYPE_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
NAME varchar(20) NOT NULL,
ACTIVE tinyint(1) NOT NULL,
CREATED_BY INT(10) UNSIGNED NOT NULL,
CREATED_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
UPDATED_BY INT(10) UNSIGNED NOT NULL,
UPDATED_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
PRIMARY KEY (ITEM_VAT_TYPE_ID),
KEY FK_IVT_CREATED_BY (CREATED_BY),
KEY FK_IVT_UPDATED_BY (UPDATED_BY),
CONSTRAINT FK_IVT_CREATED_BY FOREIGN KEY (CREATED_BY) REFERENCES USER (USER_ID),
CONSTRAINT FK_IVT_UPDATED_BY FOREIGN KEY (UPDATED_BY) REFERENCES USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;