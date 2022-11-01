
-- Description: SQL script in creating user information table. 
-- TODO: update the foreign key.
CREATE TABLE USER_INFO (
	USER_INFO_ID INT(10) unsigned not NULL,
	USER_ID INT(10) unsigned not null,
	FIRST_NAME VARCHAR(30) NOT NULL,
	LAST_NAME VARCHAR(50) NOT NULL,
	CONTACT_NUMBER VARCHAR(20) NOT NULL,
	ADDRESS VARCHAR(150) DEFAULT NULL,
	CREATED_BY INT(10) DEFAULT NULL,
  	CREATED_DATE TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 	UPDATED_BY INT(10) DEFAULT NULL,
  	UPDATED_DATE TIMESTAMP NOT NULL DEFAULT "0000-00-00 00:00:00",
	PRIMARY KEY (USER_INFO_ID),
	KEY FK_USER_INFO_USER_ID (USER_ID),
	CONSTRAINT FK_USER_INFO_USER_ID FOREIGN KEY (USER_ID) REFERENCES USER (USER_ID)
)ENGINE=InnoDB DEFAULT CHARSET=latin1;