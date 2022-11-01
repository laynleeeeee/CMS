
-- Description: Create script for POSITION table

DROP TABLE IF EXISTS POSITION;

CREATE TABLE POSITION (
	POSITION_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	NAME varchar(40) NOT NULL,
	CREATED_BY int(10) unsigned NOT NULL,
	CREATED_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	UPDATED_BY int(10) unsigned NOT NULL,
	UPDATED_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
	ACTIVE tinyint(1) NOT NULL,
	PRIMARY KEY (POSITION_ID),
	KEY FK_POSITION_CREATED_BY (CREATED_BY),
	KEY FK_POSITION_UPDATED_BY (UPDATED_BY),
	CONSTRAINT FK_POSITION_CREATED_BY FOREIGN KEY (CREATED_BY) REFERENCES USER (USER_ID),
	CONSTRAINT FK_POSITION_UPDATED_BY FOREIGN KEY (UPDATED_BY) REFERENCES USER (USER_ID)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;