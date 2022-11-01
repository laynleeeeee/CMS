
-- Description: sql script that will handle the login status of the users.  

DROP TABLE IF EXISTS USER_LOGIN_STATUS;

CREATE TABLE `USER_LOGIN_STATUS` (
  `USER_LOGIN_STATUS_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `USER_ID` int(10) unsigned NOT NULL,
  `SUCCESSFUL_LOGIN_ATTEMPT` int(10) unsigned NOT NULL,
  `FAILED_LOGIN_ATTEMPT` int(2) DEFAULT NULL,
  `BLOCK_USER` tinyint(1) NOT NULL,
  `LAST_LOGIN` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`USER_LOGIN_STATUS_ID`),
  KEY `FK_USER_LOG_STATUS_USER` (`USER_ID`),
  CONSTRAINT `FK_USER_LOG_STATUS_USER` FOREIGN KEY (`USER_ID`) REFERENCES `USER` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;