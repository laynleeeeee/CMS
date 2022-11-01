
-- Description: sql script in creating INVOICE_CLASSIFICATION table.

DROP TABLE IF EXISTS INVOICE_CLASSIFICATION;

CREATE TABLE `INVOICE_CLASSIFICATION` (
  `INVOICE_CLASSIFICATION_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `EB_SL_KEY_ID` int(10) unsigned NOT NULL,
  `NAME` varchar(50) NOT NULL,
  `ACTIVE` tinyint(1) NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`INVOICE_CLASSIFICATION_ID`),
  KEY `FK_INVOICE_CLASSIFICATION_EB_SL_KEY_ID` (`EB_SL_KEY_ID`),
  KEY `FK_INVOICE_CLASSIFICATION_CREATED_BY` (`CREATED_BY`),
  KEY `FK_INVOICE_CLASSIFICATION_UPDATED_BY` (`UPDATED_BY`),
  CONSTRAINT `FK_INVOICE_CLASSIFICATION_CREATED_BY` FOREIGN KEY (`CREATED_BY`) REFERENCES `USER` (`USER_ID`),
  CONSTRAINT `FK_INVOICE_CLASSIFICATION_EB_SL_KEY_ID` FOREIGN KEY (`EB_SL_KEY_ID`) REFERENCES `EB_SL_KEY` (`EB_SL_KEY_ID`),
  CONSTRAINT `FK_INVOICE_CLASSIFICATION_UPDATED_BY` FOREIGN KEY (`UPDATED_BY`) REFERENCES `USER` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;