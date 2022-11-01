
-- Description: Create script for LOCATION Table.

CREATE TABLE LOCATION(
LOCATION_ID INT(10) unsigned NOT NULL AUTO_INCREMENT,
NAME varchar(25) NOT NULL,
ACTIVE tinyint(1) DEFAULT '1',
CREATED_BY INT(10) unsigned NOT NULL,
CREATED_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
UPDATED_BY INT(10) unsigned NOT NULL,
UPDATED_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
PRIMARY KEY (LOCATION_ID),
KEY FK_LOCATION_CREATED_BY (CREATED_BY),
KEY FK_LOCATION_UPDATED_BY (UPDATED_BY),
CONSTRAINT FK_LOCATION_CREATED_BY FOREIGN KEY (CREATED_BY) REFERENCES USER (USER_ID),
CONSTRAINT FK_LOCATION_UPDATED_BY FOREIGN KEY (UPDATED_BY) REFERENCES USER (USER_ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;