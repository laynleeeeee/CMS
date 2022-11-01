
-- Description: sql script in creating CAP_AR_LINE table.

DROP TABLE IF EXISTS CAP_AR_LINE;
CREATE TABLE `CAP_AR_LINE` (
  `CAP_AR_LINE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CUSTOMER_ADVANCE_PAYMENT_ID` int(10) unsigned NOT NULL,
  `EB_OBJECT_ID` int(10) unsigned DEFAULT NULL,
  `AR_LINE_SETUP_ID` int(10) unsigned NOT NULL,
  `DISCOUNT_TYPE_ID` int(10) unsigned DEFAULT NULL,
  `DISCOUNT_VALUE` double DEFAULT '0',
  `DISCOUNT` double DEFAULT '0',
  `TAX_TYPE_ID` int(10) unsigned DEFAULT NULL,
  `VAT_AMOUNT` double DEFAULT '0',
  `QUANTITY` double DEFAULT '0',
  `UNITOFMEASUREMENT_ID` int(10) unsigned DEFAULT NULL,
  `UP_AMOUNT` double DEFAULT '0',
  `AMOUNT` double DEFAULT '0',
  PRIMARY KEY (`CAP_AR_LINE_ID`),
  KEY `FK_CAP_AL_CAP_ID` (`CUSTOMER_ADVANCE_PAYMENT_ID`),
  KEY `FK_CAP_AL_AR_LINE_SETUP_ID` (`AR_LINE_SETUP_ID`),
  KEY `FK_CAP_AR_LINE_EB_OBJECT_ID` (`EB_OBJECT_ID`),
  CONSTRAINT `FK_CAP_AL_AR_LINE_SETUP_ID` FOREIGN KEY (`AR_LINE_SETUP_ID`) REFERENCES `AR_LINE_SETUP` (`AR_LINE_SETUP_ID`),
  CONSTRAINT `FK_CAP_AL_CAP_ID` FOREIGN KEY (`CUSTOMER_ADVANCE_PAYMENT_ID`) REFERENCES `CUSTOMER_ADVANCE_PAYMENT` (`CUSTOMER_ADVANCE_PAYMENT_ID`),
  CONSTRAINT `FK_CAP_AL_EB_OBJECT_ID` FOREIGN KEY (`EB_OBJECT_ID`) REFERENCES `EB_OBJECT` (`EB_OBJECT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;