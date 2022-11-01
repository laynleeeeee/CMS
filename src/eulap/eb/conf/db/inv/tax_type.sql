
-- Description	: SQL script to create TAX_TYPE table

DROP TABLE IF EXISTS TAX_TYPE;

CREATE TABLE TAX_TYPE (
	TAX_TYPE_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	NAME varchar(20) NOT NULL,
	ACTIVE tinyint(1) NOT NULL,
	PRIMARY KEY (TAX_TYPE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;