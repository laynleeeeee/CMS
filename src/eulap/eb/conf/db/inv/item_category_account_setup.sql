
-- Description: SQL script in creating ITEM_CATEGORY_ACCOUNT_SETUP table.

DROP TABLE IF EXISTS ITEM_CATEGORY_ACCOUNT_SETUP;

CREATE TABLE `ITEM_CATEGORY_ACCOUNT_SETUP` (
  `ITEM_CATEGORY_ACCOUNT_SETUP_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `ITEM_CATEGORY_ID` int(10) unsigned NOT NULL,
  `COST_ACCOUNT` int(10) unsigned NOT NULL,
  `INVENTORY_ACCOUNT` int(10) unsigned NOT NULL,
  `SALES_ACCOUNT` int(10) unsigned NOT NULL,
  `SALES_DISCOUNT_ACCOUNT` int(10) unsigned NOT NULL,
  `SALES_RETURN_ACCOUNT` int(10) unsigned NOT NULL,
  `ACTIVE` tinyint(1) NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`ITEM_CATEGORY_ACCOUNT_SETUP_ID`),
  KEY `FK_ICAS_COMPANY` (`COMPANY_ID`),
  KEY `FK_ICAS_COST_ACCOUNT` (`COST_ACCOUNT`),
  KEY `FK_ICAS_INVENTORY_ACCOUNT` (`INVENTORY_ACCOUNT`),
  KEY `FK_ICAS_SALES_ACCOUNT` (`SALES_ACCOUNT`),
  KEY `FK_ICAS_SALES_DISCOUNT_ACCOUNT` (`SALES_DISCOUNT_ACCOUNT`),
  KEY `FK_ICAS_SALES_RETURN_ACCOUNT` (`SALES_RETURN_ACCOUNT`),
  KEY `FK_ICAS_ITEM_CATEGORY_ID` (`ITEM_CATEGORY_ID`),
  KEY `FK_ICAS_CREATED_BY` (`CREATED_BY`),
  KEY `FK_ICAS_UPDATED_BY` (`UPDATED_BY`),
  CONSTRAINT `FK_ICAS_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`COMPANY_ID`),
  CONSTRAINT `FK_ICAS_COST_ACCOUNT` FOREIGN KEY (`COST_ACCOUNT`) REFERENCES `ACCOUNT_COMBINATION` (`ACCOUNT_COMBINATION_ID`),
  CONSTRAINT `FK_ICAS_CREATED_BY` FOREIGN KEY (`CREATED_BY`) REFERENCES `USER` (`USER_ID`),
  CONSTRAINT `FK_ICAS_INVENTORY_ACCOUNT` FOREIGN KEY (`INVENTORY_ACCOUNT`) REFERENCES `ACCOUNT_COMBINATION` (`ACCOUNT_COMBINATION_ID`),
  CONSTRAINT `FK_ICAS_ITEM_CATEGORY_ID` FOREIGN KEY (`ITEM_CATEGORY_ID`) REFERENCES `ITEM_CATEGORY` (`ITEM_CATEGORY_ID`),
  CONSTRAINT `FK_ICAS_SALES_ACCOUNT` FOREIGN KEY (`SALES_ACCOUNT`) REFERENCES `ACCOUNT_COMBINATION` (`ACCOUNT_COMBINATION_ID`),
  CONSTRAINT `FK_ICAS_SALES_DISCOUNT_ACCOUNT` FOREIGN KEY (`SALES_DISCOUNT_ACCOUNT`) REFERENCES `ACCOUNT_COMBINATION` (`ACCOUNT_COMBINATION_ID`),
  CONSTRAINT `FK_ICAS_SALES_RETURN_ACCOUNT` FOREIGN KEY (`SALES_RETURN_ACCOUNT`) REFERENCES `ACCOUNT_COMBINATION` (`ACCOUNT_COMBINATION_ID`),
  CONSTRAINT `FK_ICAS_UPDATED_BY` FOREIGN KEY (`UPDATED_BY`) REFERENCES `USER` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;