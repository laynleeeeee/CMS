
-- Description: SQL script in creating UG_M_ACCESS_RIGHT table.

CREATE TABLE `UG_M_ACCESS_RIGHT` (
  `UG_M_ACCESS_RIGHT_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `USER_GROUP_ID` int(10) unsigned NOT NULL,
  `PRODUCT_CODE_ID` int(10) unsigned NOT NULL,
  `ACCESS_RIGHT_FLAG` int(10) unsigned NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`UG_M_ACCESS_RIGHT_ID`) USING BTREE,
  KEY `FK_UG_M_USER_GROUP_ID` (`USER_GROUP_ID`),
  KEY `FK_UG_M_PRODUCT_CODE_ID` (`PRODUCT_CODE_ID`),
  CONSTRAINT `FK_UG_M_USER_GROUP_ID` FOREIGN KEY (`USER_GROUP_ID`) REFERENCES `USER_GROUP` (`USER_GROUP_ID`),
  CONSTRAINT `FK_UG_M_PRODUCT_CODE_ID` FOREIGN KEY (`PRODUCT_CODE_ID`) REFERENCES `PRODUCT_CODE` (`PRODUCT_CODE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1