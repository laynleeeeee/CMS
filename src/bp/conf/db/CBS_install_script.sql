-- MySQL dump 10.13  Distrib 5.1.61, for debian-linux-gnu (x86_64)
-- 
-- Host: localhost    Database: CBS

-- Description: Tables and routines only.  
-- ------------------------------------------------------
-- Server version	5.1.61-0ubuntu0.11.10.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ACCOUNT_PAYABLE`
--

DROP TABLE IF EXISTS `ACCOUNT_PAYABLE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACCOUNT_PAYABLE` (
  `ACCOUNT_PAYABLE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `PAYABLE_PROFILE_ID` int(10) unsigned NOT NULL,
  `DATE` date NOT NULL,
  `DUE_DATE` date NOT NULL,
  `PAID_DATE` date DEFAULT NULL,
  `REFERENCE_ID` varchar(20) NOT NULL,
  `DESCRIPTION` varchar(150) NOT NULL,
  `AMOUNT` double NOT NULL,
  `PAID` tinyint(1) NOT NULL,
  `DELETED` tinyint(1) NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`ACCOUNT_PAYABLE_ID`),
  KEY `ACCT_PAYABLE_PROFILE_ID` (`PAYABLE_PROFILE_ID`),
  CONSTRAINT `ACCT_PAYABLE_PROFILE_ID` FOREIGN KEY (`PAYABLE_PROFILE_ID`) REFERENCES `PAYABLE_PROFILE` (`PAYABLE_PROFILE_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ACCOUNT_RECEIVABLE`
--

DROP TABLE IF EXISTS `ACCOUNT_RECEIVABLE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACCOUNT_RECEIVABLE` (
  `ACCOUNT_RECEIVABLE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `DATE` date NOT NULL,
  `REFERENCE_ID` varchar(20) NOT NULL,
  `DESCRIPTION` varchar(150) NOT NULL,
  `PRINCIPAL` double NOT NULL DEFAULT '0',
  `AMOUNT` double NOT NULL,
  `EARNED_INTEREST` double NOT NULL,
  `DUE_DATE` date DEFAULT NULL,
  `PAID` tinyint(1) NOT NULL,
  `DELETED` tinyint(1) NOT NULL,
  `CUSTOMER_ID` int(10) unsigned NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`ACCOUNT_RECEIVABLE_ID`),
  KEY `FK_ACCOUNT_RECEIVABLE_CUSTOMER` (`CUSTOMER_ID`),
  CONSTRAINT `FK_ACCOUNT_RECEIVABLE_CUSTOMER` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `CUSTOMER` (`CUSTOMER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=12421 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `COMPANY`
--

DROP TABLE IF EXISTS `COMPANY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COMPANY` (
  `COMPANY_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `NAME` varchar(50) NOT NULL,
  `ADDRESS` varchar(150) NOT NULL,
  `CONTACT_NUMBER` varchar(20) NOT NULL,
  `EMAIL_ADDRESS` varchar(50) DEFAULT NULL,
  `ACTIVE` tinyint(1) NOT NULL,
  `CREATED_BY` int(10) unsigned DEFAULT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned DEFAULT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`COMPANY_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=96 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CUSTOMER`
--

DROP TABLE IF EXISTS `CUSTOMER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CUSTOMER` (
  `CUSTOMER_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `NAME` varchar(50) NOT NULL,
  `CONTACT_NUMBER` varchar(20) NOT NULL,
  `ADDRESS` varchar(150) NOT NULL,
  `EMAIL_ADDRESS` varchar(50) DEFAULT NULL,
  `ACTIVE` tinyint(1) NOT NULL,
  `DELETED` tinyint(1) NOT NULL,
  `CREATED_BY` int(10) unsigned DEFAULT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned DEFAULT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`CUSTOMER_ID`),
  KEY `FK_CUSTOMER_COMPANY` (`COMPANY_ID`),
  CONSTRAINT `FK_CUSTOMER_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`COMPANY_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=475 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CUSTOMER_ACCT_SETTING`
--

DROP TABLE IF EXISTS `CUSTOMER_ACCT_SETTING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CUSTOMER_ACCT_SETTING` (
  `CUSTOMER_ACCT_SETTING_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CUSTOMER_ID` int(10) unsigned NOT NULL,
  `INTEREST_RATE` double NOT NULL,
  `MAX_AMOUNT` double NOT NULL,
  `CREATED_BY` int(10) unsigned DEFAULT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned DEFAULT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`CUSTOMER_ACCT_SETTING_ID`),
  KEY `FK_CUSTOMER_ACCT_SETTING_CUSTOMER` (`CUSTOMER_ID`),
  CONSTRAINT `FK_CUSTOMER_ACCT_SETTING_CUSTOMER` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `CUSTOMER` (`CUSTOMER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=874 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `EXPENSE`
--

DROP TABLE IF EXISTS `EXPENSE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `EXPENSE` (
  `EXPENSE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `EXPENSE_TYPE_ID` int(10) unsigned NOT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `DATE` date NOT NULL,
  `REFERENCE_ID` varchar(20) NOT NULL,
  `DESCRIPTION` varchar(150) NOT NULL,
  `AMOUNT` double NOT NULL,
  `DELETED` tinyint(1) NOT NULL,
  `CREATED_BY` int(10) unsigned DEFAULT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned DEFAULT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`EXPENSE_ID`),
  KEY `FK_EXPENSE_EXPENSE_TYPE` (`COMPANY_ID`),
  CONSTRAINT `FK_EXPENSE_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`COMPANY_ID`),
  CONSTRAINT `FK_EXPENSE_EXPENSE_TYPE` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`COMPANY_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `EXPENSE_TYPE`
--

DROP TABLE IF EXISTS `EXPENSE_TYPE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `EXPENSE_TYPE` (
  `EXPENSE_TYPE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `NAME` varchar(50) NOT NULL,
  `DESCRIPTION` varchar(150) NOT NULL,
  `CREATED_BY` int(10) unsigned DEFAULT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned DEFAULT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`EXPENSE_TYPE_ID`),
  KEY `EXPENSE_TYPE_COMPANY_ID` (`COMPANY_ID`),
  CONSTRAINT `EXPENSE_TYPE_COMPANY_ID` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`COMPANY_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PAID_RECEIVABLE`
--

DROP TABLE IF EXISTS `PAID_RECEIVABLE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PAID_RECEIVABLE` (
  `PAID_RECEIVABLE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `PAYMENT_ID` int(10) unsigned NOT NULL,
  `ACCOUNT_RECEIVABLE_ID` int(10) unsigned NOT NULL,
  `PAID_PRINCIPAL` double NOT NULL,
  `PAID_INTEREST` double NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`PAID_RECEIVABLE_ID`),
  KEY `FK_PAYMENT` (`PAYMENT_ID`),
  KEY `FK_ACCOUNT_RECEIVABLE` (`ACCOUNT_RECEIVABLE_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=214 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PAYABLE_PROFILE`
--

DROP TABLE IF EXISTS `PAYABLE_PROFILE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PAYABLE_PROFILE` (
  `PAYABLE_PROFILE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `NAME` varchar(50) NOT NULL,
  `CONTACT_NUMBER` varchar(20) NOT NULL,
  `ADDRESS` varchar(50) NOT NULL,
  `EMAIL_ADDRESS` varchar(50) DEFAULT NULL,
  `ACTIVE` tinyint(1) NOT NULL,
  `DELETED` tinyint(1) NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`PAYABLE_PROFILE_ID`) USING BTREE,
  KEY `FK_ACCT_PAYABLE_PROFILE_COMPANY` (`COMPANY_ID`),
  CONSTRAINT `FK_ACCT_PAYABLE_PROFILE_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`COMPANY_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PAYMENT`
--

DROP TABLE IF EXISTS `PAYMENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PAYMENT` (
  `PAYMENT_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `DATE` date NOT NULL,
  `DESCRIPTION` varchar(150) NOT NULL,
  `REFERENCE_ID` varchar(20) NOT NULL,
  `AMOUNT` double NOT NULL,
  `PAYMENT_CHANGE` double NOT NULL,
  `DELETED` tinyint(1) NOT NULL,
  `CUSTOMER_ID` int(10) unsigned NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`PAYMENT_ID`),
  KEY `FK_DEBT_PAYMENT_CUSTOMER` (`CUSTOMER_ID`),
  CONSTRAINT `FK_DEBT_PAYMENT_CUSTOMER` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `CUSTOMER` (`CUSTOMER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PAYMENT_RECEIVABLE`
--

DROP TABLE IF EXISTS `PAYMENT_RECEIVABLE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PAYMENT_RECEIVABLE` (
  `PAYMENT_RECEIVABLE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `PAYMENT_ID` int(10) unsigned NOT NULL,
  `ACCOUNT_RECEIVABLE_ID` int(10) unsigned NOT NULL,
  `CREATED_BY` int(10) unsigned NOT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) unsigned NOT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`PAYMENT_RECEIVABLE_ID`),
  KEY `FK_PAYMENT` (`PAYMENT_ID`),
  KEY `FK_ACCOUNT_RECEIVABLE` (`ACCOUNT_RECEIVABLE_ID`),
  CONSTRAINT `FK_ACCOUNT_RECEIVABLE` FOREIGN KEY (`ACCOUNT_RECEIVABLE_ID`) REFERENCES `ACCOUNT_RECEIVABLE` (`ACCOUNT_RECEIVABLE_ID`),
  CONSTRAINT `FK_PAYMENT` FOREIGN KEY (`PAYMENT_ID`) REFERENCES `PAYMENT` (`PAYMENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PAYMENT_RECEIVABLE_JOIN`
--

DROP TABLE IF EXISTS `PAYMENT_RECEIVABLE_JOIN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PAYMENT_RECEIVABLE_JOIN` (
  `ACCOUNT_RECEIVABLE_ID` int(10) unsigned NOT NULL,
  `PAYMENT_RECEIVABLE_ID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`ACCOUNT_RECEIVABLE_ID`,`PAYMENT_RECEIVABLE_ID`),
  KEY `FK_PAYMENT_RECEIVABLE_ID` (`PAYMENT_RECEIVABLE_ID`),
  CONSTRAINT `FK_ACCOUNT_RECEIVABLE_ID` FOREIGN KEY (`ACCOUNT_RECEIVABLE_ID`) REFERENCES `ACCOUNT_RECEIVABLE` (`ACCOUNT_RECEIVABLE_ID`),
  CONSTRAINT `FK_PAYMENT_RECEIVABLE_ID` FOREIGN KEY (`PAYMENT_RECEIVABLE_ID`) REFERENCES `PAYMENT_RECEIVABLE` (`PAYMENT_RECEIVABLE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `USER`
--

DROP TABLE IF EXISTS `USER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `USER` (
  `USER_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `USER_NAME` varchar(20) NOT NULL,
  `PASSWORD` blob,
  `FIRST_NAME` varchar(40) NOT NULL,
  `LAST_NAME` varchar(40) NOT NULL,
  `MIDDLE_NAME` varchar(40) NOT NULL,
  `BIRTH_DATE` date NOT NULL,
  `CONTACT_NUMBER` varchar(20) NOT NULL,
  `EMAIL_ADDRESS` varchar(50) DEFAULT NULL,
  `ADDRESS` varchar(150) NOT NULL,
  `USER_GROUP_ID` int(10) unsigned NOT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `CREATED_BY` int(10) DEFAULT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) DEFAULT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `ACTIVE` tinyint(1) NOT NULL,
  PRIMARY KEY (`USER_ID`),
  KEY `FK_USER_USER_GROUP` (`USER_GROUP_ID`),
  KEY `FK_USER_COMPANY_ID` (`COMPANY_ID`),
  CONSTRAINT `FK_USER_COMPANY_ID` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`COMPANY_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `USER_COMPANY`
--

DROP TABLE IF EXISTS `USER_COMPANY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `USER_COMPANY` (
  `USER_COMPANY_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `USER_ID` int(10) unsigned NOT NULL,
  `COMPANY_ID` int(10) unsigned NOT NULL,
  `CREATED_BY` int(10) DEFAULT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) DEFAULT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`USER_COMPANY_ID`),
  KEY `FK_USER_COMPANY_USER` (`USER_ID`),
  KEY `FK_USER_COMPANY_COMPANY` (`COMPANY_ID`),
  CONSTRAINT `FK_USER_COMPANY_COMPANY` FOREIGN KEY (`COMPANY_ID`) REFERENCES `COMPANY` (`COMPANY_ID`),
  CONSTRAINT `FK_USER_COMPANY_USER` FOREIGN KEY (`USER_ID`) REFERENCES `USER` (`USER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `USER_GROUP`
--

DROP TABLE IF EXISTS `USER_GROUP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `USER_GROUP` (
  `USER_GROUP_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `NAME` varchar(20) NOT NULL,
  `DESCRIPTION` varchar(150) DEFAULT NULL,
  `ACTIVE` tinyint(1) NOT NULL,
  `CREATED_BY` int(10) DEFAULT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) DEFAULT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`USER_GROUP_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `USER_INFO`
--

DROP TABLE IF EXISTS `USER_INFO`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `USER_INFO` (
  `USER_INFO_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `USER_ID` int(10) unsigned NOT NULL,
  `FIRST_NAME` varchar(30) NOT NULL,
  `LAST_NAME` varchar(50) NOT NULL,
  `CONTACT_NUMBER` varchar(20) NOT NULL,
  `ADDRESS` varchar(150) DEFAULT NULL,
  `CREATED_BY` int(10) DEFAULT NULL,
  `CREATED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_BY` int(10) DEFAULT NULL,
  `UPDATED_DATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`USER_INFO_ID`),
  KEY `FK_USER_INFO_USER_ID` (`USER_ID`),
  CONSTRAINT `FK_USER_INFO_USER_ID` FOREIGN KEY (`USER_ID`) REFERENCES `USER` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `USER_LOGIN_STATUS`
--

DROP TABLE IF EXISTS `USER_LOGIN_STATUS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'CBS'
--
/*!50003 DROP FUNCTION IF EXISTS `EARNED_INTEREST` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 FUNCTION `EARNED_INTEREST`(amount DOUBLE, interestRate DOUBLE, currentDate Date, dueDate Date) RETURNS double
BEGIN
		DECLARE earnedInterest DOUBLE;
		DECLARE multiplier DOUBLE;
		DECLARE dateDiff INT;
		SET multiplier = interestRate /100/365;
		IF dueDate >= currentDate THEN SET dateDiff = 0;
		ELSE SET dateDiff = DATEDIFF(currentDate,dueDate);
		END IF;
		SET earnedInterest = amount * multiplier * dateDiff;
		RETURN earnedInterest;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getAcctReceivableTotAmt` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getAcctReceivableTotAmt`(IN customerId int)
BEGIN
	SELECT SUM(AMOUNT) FROM ACCOUNT_RECEIVABLE WHERE 
	CUSTOMER_ID = customerId AND DELETED = 0;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getCompanyIdByName` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getCompanyIdByName`(IN name varchar(50))
BEGIN
	SELECT COMPANY_ID FROM COMPANY WHERE NAME = name;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getCurrentAccountBalance` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getCurrentAccountBalance`(IN customerId int)
BEGIN
SELECT sum(EARNED_INTEREST(AR.AMOUNT, CAS.INTEREST_RATE, NOW(), AR.DUE_DATE) +  AR.AMOUNT)
FROM ACCOUNT_RECEIVABLE AR
	INNER JOIN (SELECT CUSTOMER_ID, INTEREST_RATE, MAX_AMOUNT FROM CUSTOMER_ACCT_SETTING) CAS ON AR.CUSTOMER_ID = CAS.CUSTOMER_ID
	INNER JOIN (SELECT CUSTOMER_ID, NAME, COMPANY_ID, ADDRESS, CONTACT_NUMBER FROM CUSTOMER) C ON AR.CUSTOMER_ID = C.CUSTOMER_ID
WHERE AR.DELETED = 0
AND PAID = 0
AND C.CUSTOMER_ID = customerId;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getCustomerAccountTable` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getCustomerAccountTable`(IN customerId int)
BEGIN
	SELECT ACCOUNT_RECEIVABLE_ID, TYPE, DATE, REFERENCE_ID, DESCRIPTION, RECEIVABLE,
	WITH_INTEREST, PAYMENT
	FROM ( SELECT ACCOUNT_RECEIVABLE_ID, TYPE, DATE, REFERENCE_ID,  DESCRIPTION, 
	"" as RECEIVABLE, "" as WITH_INTEREST, AMOUNT as PAYMENT FROM ACCOUNT_RECEIVABLE 
	WHERE CUSTOMER_ID = customerId AND TYPE = 'PAYMENT' AND DELETED = 0 
	UNION ALL 
	SELECT AR.ACCOUNT_RECEIVABLE_ID, AR.TYPE, AR.DATE, AR.REFERENCE_ID,  AR.DESCRIPTION,
	AR.AMOUNT as RECEIVABLE, 
	((((CAS.INTEREST_RATE/ 100) / 365.25) * DATEDIFF(NOW(), DUE_DATE) + 1) * AR.AMOUNT) 
	AS WITH_INTEREST, "" as PAYMENT FROM ACCOUNT_RECEIVABLE AR 
	INNER JOIN (SELECT CUSTOMER_ID, INTEREST_RATE FROM CUSTOMER_ACCT_SETTING) AS CAS
	ON AR.CUSTOMER_ID = CAS.CUSTOMER_ID WHERE AR.CUSTOMER_ID = customerId
	AND TYPE = 'RECEIVABLE' AND DELETED = 0) table1 ORDER BY DATE;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getCustomerIdByName` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getCustomerIdByName`(IN name varchar(50))
BEGIN
	SELECT CUSTOMER_ID FROM CUSTOMER WHERE NAME = name;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getCustomerListing` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getCustomerListing`(IN companyId int)
BEGIN
	SELECT C.CUSTOMER_ID, C.NAME, C.ADDRESS, C.CONTACT_NUMBER,
	SUM(((((CAS.INTEREST_RATE  / 100) / 365.25) * DATEDIFF(NOW(), AR.DUE_DATE) + 1) * AR.AMOUNT)) 
	AS CURRENT_BALANCE FROM CUSTOMER C INNER JOIN CUSTOMER_ACCT_SETTING CAS ON (C.CUSTOMER_ID = CAS.CUSTOMER_ID)
	LEFT JOIN (SELECT * FROM ACCOUNT_RECEIVABLE WHERE DELETED = 0) AR 
	ON (C.CUSTOMER_ID = AR.CUSTOMER_ID) WHERE C.COMPANY_ID = companyId
	GROUP BY (C.CUSTOMER_ID);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getCustomerReceivables` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getCustomerReceivables`(IN customerId int)
BEGIN
SELECT AR.ACCOUNT_RECEIVABLE_ID, AR.DATE, AR.DUE_DATE, AR.REFERENCE_ID, AR.DESCRIPTION, AR.AMOUNT,
	((((CAS.INTEREST_RATE/ 100) / 365.25) * DATEDIFF(NOW(), DUE_DATE) + 1) * AR.AMOUNT) AS WITH_INTEREST
FROM ACCOUNT_RECEIVABLE AR
	INNER JOIN (SELECT CUSTOMER_ID, INTEREST_RATE, MAX_AMOUNT FROM CUSTOMER_ACCT_SETTING) CAS ON AR.CUSTOMER_ID = CAS.CUSTOMER_ID
WHERE AR.CUSTOMER_ID = customerId
AND PAID = 0
UNION ALL
SELECT AR.ACCOUNT_RECEIVABLE_ID, AR.DATE, AR.DUE_DATE, AR.REFERENCE_ID, AR.DESCRIPTION, PR.REMAINING_BALANCE AS AMOUNT,
((((CAS.INTEREST_RATE/ 100) / 365.25) * DATEDIFF(NOW(), DUE_DATE) + 1) * PR.REMAINING_BALANCE) AS WITH_INTEREST FROM PAID_RECEIVABLE PR
	INNER JOIN (SELECT ACCOUNT_RECEIVABLE_ID,REFERENCE_ID, DESCRIPTION, AMOUNT, CUSTOMER_ID, DUE_DATE, DATE FROM ACCOUNT_RECEIVABLE) AR ON PR.ACCOUNT_RECEIVABLE_ID = AR.ACCOUNT_RECEIVABLE_ID
	INNER JOIN (SELECT CUSTOMER_ID, INTEREST_RATE, MAX_AMOUNT FROM CUSTOMER_ACCT_SETTING) CAS ON AR.CUSTOMER_ID = CAS.CUSTOMER_ID
WHERE PARTIAL_PAYMENT = 1
AND AR.AMOUNT >= PR.REMAINING_BALANCE
AND AR.CUSTOMER_ID = customerId
UNION ALL
SELECT AR.ACCOUNT_RECEIVABLE_ID, AR.DATE, AR.DUE_DATE, AR.REFERENCE_ID, AR.DESCRIPTION, PR.REMAINING_BALANCE AS AMOUNT,
((((CAS.INTEREST_RATE/ 100) / 365.25) * DATEDIFF(NOW(), DUE_DATE)) * AR.AMOUNT) + (AR.AMOUNT - PR.REMAINING_BALANCE) AS WITH_INTEREST FROM PAID_RECEIVABLE PR
	INNER JOIN (SELECT ACCOUNT_RECEIVABLE_ID,REFERENCE_ID, DESCRIPTION, AMOUNT, CUSTOMER_ID, DUE_DATE, DATE FROM ACCOUNT_RECEIVABLE) AR ON PR.ACCOUNT_RECEIVABLE_ID = AR.ACCOUNT_RECEIVABLE_ID
	INNER JOIN (SELECT CUSTOMER_ID, NAME, COMPANY_ID, ADDRESS, CONTACT_NUMBER  FROM CUSTOMER) C ON AR.CUSTOMER_ID = C.CUSTOMER_ID
	INNER JOIN (SELECT CUSTOMER_ID, INTEREST_RATE, MAX_AMOUNT FROM CUSTOMER_ACCT_SETTING) CAS ON AR.CUSTOMER_ID = CAS.CUSTOMER_ID
WHERE PARTIAL_PAYMENT = 1
AND AR.AMOUNT < PR.REMAINING_BALANCE
AND AR.CUSTOMER_ID = customerId;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getFailedLoginAttempts` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getFailedLoginAttempts`(IN userId int)
BEGIN
	SELECT FAILED_LOGIN_ATTEMPT FROM  USER_LOGIN_STATUS 
	WHERE USER_ID = userId;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getInterestRate` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getInterestRate`(IN customerId int )
BEGIN
	SELECT INTEREST_RATE FROM CUSTOMER_ACCT_SETTING WHERE CUSTOMER_ID = customerId; 
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getLastPaymentAmount` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getLastPaymentAmount`(IN customerId int)
BEGIN
	DECLARE lastPaymentDate DATE;
	DECLARE lastPaymentAmount DOUBLE;
	
	SELECT MAX(DATE) FROM PAYMENT WHERE CUSTOMER_ID = customerId 
	AND DELETED = 0  INTO lastPaymentDate;

	 SELECT AMOUNT FROM PAYMENT WHERE CUSTOMER_ID = customerId 
	AND DELETED = 0 AND DATE = lastPaymentDate 
	INTO lastPaymentAmount;

	SELECT  lastPaymentAmount;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getLastPaymentDate` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getLastPaymentDate`(IN customerId int)
BEGIN
	SELECT MAX(DATE) FROM PAYMENT WHERE CUSTOMER_ID = customerId 
	AND DELETED = 0;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getMaxAllowableAmount` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getMaxAllowableAmount`(IN customerId int)
BEGIN
	SELECT MAX_AMOUNT FROM CUSTOMER_ACCT_SETTING 
	WHERE CUSTOMER_ID = customerId;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getPaymentTotAmt` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getPaymentTotAmt`(IN customerId int)
BEGIN
	SELECT SUM(AMOUNT) FROM PAYMENT WHERE CUSTOMER_ID = customerId 
	AND DELETED = 0;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getTotalInterestEarned` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getTotalInterestEarned`(IN companyId int)
BEGIN
SELECT SUM(EARNED_INTEREST(AR.AMOUNT, CAS.INTEREST_RATE, NOW(), AR.DUE_DATE)) AS EARNED_INTEREST
FROM ACCOUNT_RECEIVABLE AR
	INNER JOIN (SELECT CUSTOMER_ID, INTEREST_RATE, MAX_AMOUNT FROM CUSTOMER_ACCT_SETTING) CAS ON AR.CUSTOMER_ID = CAS.CUSTOMER_ID
	INNER JOIN (SELECT CUSTOMER_ID, NAME, COMPANY_ID, ADDRESS, CONTACT_NUMBER FROM CUSTOMER) C ON AR.CUSTOMER_ID = C.CUSTOMER_ID
WHERE AR.DELETED = 0
AND PAID = 0
AND C.COMPANY_ID = companyId;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getUserGroupIdByName` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getUserGroupIdByName`(IN name varchar(50))
BEGIN
	SELECT USER_GROUP_ID FROM USER_GROUP WHERE NAME = name;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getUserIdByUserName` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `getUserIdByUserName`(IN userName VARCHAR(30))
BEGIN
	SELECT USER_ID FROM USER WHERE USER_NAME = userName;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `isExistingUser` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `isExistingUser`(IN userId int)
BEGIN
	SELECT USER_ID FROM  USER_LOGIN_STATUS WHERE USER_ID = userId;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `validateUser` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = '' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50020 DEFINER=`cmsadmin`@`%`*/ /*!50003 PROCEDURE `validateUser`(IN username varchar(50), IN password varchar(50))
BEGIN
	SELECT USER_NAME FROM USER
	WHERE USER_NAME LIKE '%username%'  
	AND PASSWORD LIKE '%SHA1 (password)%';
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-04-12 18:30:25
