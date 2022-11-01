
-- Description: SQL script in creating the EMPLOYEE_TYPE table

DROP TABLE IF EXISTS EMPLOYEE_TYPE;

CREATE TABLE EMPLOYEE_TYPE (
 EMPLOYEE_TYPE_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
 NAME varchar(25) NOT NULL,
 ACTIVE tinyint(1) NOT NULL,
 CREATED_BY int(10) unsigned NOT NULL,
 CREATED_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
 UPDATED_BY int(10) unsigned NOT NULL,
 UPDATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
 PRIMARY KEY (EMPLOYEE_TYPE_ID),
 KEY FK_EMPLOYEE_TYPE_CREATED_BY (CREATED_BY),
 KEY FK_EMPLOYEE_TYPE_UPDATED_BY (UPDATED_BY),
 CONSTRAINT FK_EMPLOYEE_TYPE_CREATED_BY FOREIGN KEY (CREATED_BY) REFERENCES USER (USER_ID),
 CONSTRAINT FK_EMPLOYEE_TYPE_UPDATED_BY FOREIGN KEY (UPDATED_BY) REFERENCES USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;