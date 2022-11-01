
-- Description: Sql script for creating AR_RECEIPT_TRANSACTION table

DROP TABLE IF EXISTS AR_RECEIPT_TRANSACTION;

CREATE TABLE `AR_RECEIPT_TRANSACTION` (
  `AR_RECEIPT_TRANSACTION_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AR_RECEIPT_ID` int(10) unsigned NOT NULL,
  `AR_TRANSACTION_ID` int(10) unsigned NOT NULL,
  `AMOUNT` double NOT NULL,
  PRIMARY KEY (`AR_RECEIPT_TRANSACTION_ID`),
  KEY `FK_AR_RECEIPT_TRANSACTION_RECEIPT_ID` (`AR_RECEIPT_ID`),
  KEY `FK_AR_RECEIPT_TRANSACTION_TRANSACTION_ID` (`AR_TRANSACTION_ID`),
  CONSTRAINT `FK_AR_RECEIPT_TRANSACTION_RECEIPT_ID` FOREIGN KEY (`AR_RECEIPT_ID`) REFERENCES `AR_RECEIPT` (`AR_RECEIPT_ID`),
  CONSTRAINT `FK_AR_RECEIPT_TRANSACTION_TRANSACTION_ID` FOREIGN KEY (`AR_TRANSACTION_ID`) REFERENCES `AR_TRANSACTION` (`AR_TRANSACTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;