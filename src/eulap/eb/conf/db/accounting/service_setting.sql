
-- Description: sql script in creating SERVICE_SETTING table.

DROP TABLE IF EXISTS SERVICE_SETTING;

CREATE TABLE SERVICE_SETTING (
  SERVICE_SETTING_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
  NAME varchar(100) NOT NULL,
  ACTIVE tinyint(1) NOT NULL,
  ACCOUNT_COMBINATION_ID int(10) unsigned NOT NULL,
  CREATED_BY int(10) unsigned NOT NULL,
  CREATED_DATE timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  UPDATED_BY int(10) unsigned NOT NULL,
  UPDATED_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (SERVICE_SETTING_ID),
  KEY FK_SERVICE_ACCT_COMBI (ACCOUNT_COMBINATION_ID),
  KEY FK_SERVICE_CREATED_BY (CREATED_BY),
  KEY FK_SERVICE_UPDATED_BY (UPDATED_BY),
  CONSTRAINT FK_SERVICE_ACCT_COMBI FOREIGN KEY (ACCOUNT_COMBINATION_ID) REFERENCES ACCOUNT_COMBINATION (ACCOUNT_COMBINATION_ID),
  CONSTRAINT FK_SERVICE_CREATED_BY FOREIGN KEY (CREATED_BY) REFERENCES USER (USER_ID),
  CONSTRAINT FK_SERVICE_UPDATED_BY FOREIGN KEY (UPDATED_BY) REFERENCES USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;