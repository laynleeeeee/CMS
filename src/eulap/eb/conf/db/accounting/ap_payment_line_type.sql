
-- Description: Create script for AP_PAYMENT_LINE_TYPE table.

DROP TABLE IF EXISTS AP_PAYMENT_LINE_TYPE;
CREATE TABLE AP_PAYMENT_LINE_TYPE(
AP_PAYMENT_LINE_TYPE_ID int(10) unsigned NOT NULL AUTO_INCREMENT,
NAME varchar(50) NOT NULL,
ACTIVE tinyint(1) NOT NULL,
CREATED_BY INT(10) UNSIGNED NOT NULL,
CREATED_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
UPDATED_BY INT(10) UNSIGNED NOT NULL,
UPDATED_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
PRIMARY KEY (AP_PAYMENT_LINE_TYPE_ID),
KEY FK_APLT_CREATED_BY (CREATED_BY),
KEY FK_APLT_UPDATED_BY (UPDATED_BY),
CONSTRAINT FK_APLT_CREATED_BY FOREIGN KEY (CREATED_BY) REFERENCES USER (USER_ID),
CONSTRAINT FK_APLT_UPDATED_BY FOREIGN KEY (UPDATED_BY) REFERENCES USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;