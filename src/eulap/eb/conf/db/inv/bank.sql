
-- Description	: SQL script that will create BANK table

CREATE TABLE BANK (
	BANK_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
	NAME varchar(100) NOT NULL,
	ACTIVE tinyint(1) DEFAULT 0,
	PRIMARY KEY (BANK_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;