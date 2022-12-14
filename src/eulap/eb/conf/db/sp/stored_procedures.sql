-- Stored procedures for CBS schema.


CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getAcctReceivableTotAmt`(IN customerId int)
BEGIN
	SELECT SUM(AMOUNT) FROM ACCOUNT_RECEIVABLE WHERE 
	CUSTOMER_ID = customerId AND DELETED = 0 AND TYPE = 'RECEIVABLE';
END

CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getCompanyIdByName`(IN name varchar(50))
BEGIN
	SELECT COMPANY_ID FROM COMPANY WHERE NAME = name;
END

CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getCurrentAccountBalance`(IN customerId int)
BEGIN
	SELECT SUM(EARNED_INTEREST(AR.AMOUNT, CAS.INTEREST_RATE, NOW(), AR.INTEREST_DATE) +  AR.AMOUNT + AR.EARNED_INTEREST)
	FROM ACCOUNT_RECEIVABLE AR
		INNER JOIN (SELECT CUSTOMER_ID, INTEREST_RATE, MAX_AMOUNT FROM CUSTOMER_ACCT_SETTING) CAS ON AR.CUSTOMER_ID = CAS.CUSTOMER_ID
		INNER JOIN (SELECT CUSTOMER_ID, NAME, COMPANY_ID, ADDRESS, CONTACT_NUMBER FROM CUSTOMER) C ON AR.CUSTOMER_ID = C.CUSTOMER_ID
	WHERE AR.DELETED = 0
	AND PAID = 0
	AND C.CUSTOMER_ID = customerId;
END
--
-- Get the the total interest earned per company
--
CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getTotalInterestEarned`(IN companyId int)
BEGIN
	SELECT SUM(EARNED_INTEREST(AR.AMOUNT, CAS.INTEREST_RATE, NOW(), AR.DUE_DATE)) AS EARNED_INTEREST
	FROM ACCOUNT_RECEIVABLE AR
		INNER JOIN (SELECT CUSTOMER_ID, INTEREST_RATE, MAX_AMOUNT FROM CUSTOMER_ACCT_SETTING) CAS ON AR.CUSTOMER_ID = CAS.CUSTOMER_ID
		INNER JOIN (SELECT CUSTOMER_ID, NAME, COMPANY_ID, ADDRESS, CONTACT_NUMBER FROM CUSTOMER) C ON AR.CUSTOMER_ID = C.CUSTOMER_ID
	WHERE AR.DELETED = 0
	AND PAID = 0
	AND C.COMPANY_ID = companyId
END 

CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getCustomerAccountTable`(IN customerId int)
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
END

CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getCustomerIdByName`(IN name varchar(50))
BEGIN
	SELECT CUSTOMER_ID FROM CUSTOMER WHERE NAME = name;
END

CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getCustomerListing`(IN companyId int)
BEGIN
	SELECT C.CUSTOMER_ID, C.NAME, C.ADDRESS, C.CONTACT_NUMBER,
	SUM(((((CAS.INTEREST_RATE  / 100) / 365.25) * DATEDIFF(NOW(), AR.DUE_DATE) + 1) * AR.AMOUNT)) 
	AS CURRENT_BALANCE FROM CUSTOMER C INNER JOIN CUSTOMER_ACCT_SETTING CAS ON (C.CUSTOMER_ID = CAS.CUSTOMER_ID)
	LEFT JOIN (SELECT * FROM ACCOUNT_RECEIVABLE WHERE TYPE = 'RECEIVABLE' AND DELETED = 0) AR 
	ON (C.CUSTOMER_ID = AR.CUSTOMER_ID) WHERE C.COMPANY_ID = companyId
	GROUP BY (C.CUSTOMER_ID);
END

CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getFailedLoginAttempts`(IN userId int)
BEGIN
	SELECT FAILED_LOGIN_ATTEMPT FROM  USER_LOGIN_STATUS 
	WHERE USER_ID = userId;
END

CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getInterestRate`(IN customerId int )
BEGIN
	SELECT INTEREST_RATE FROM CUSTOMER_ACCT_SETTING WHERE CUSTOMER_ID = customerId; 
END

CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getLastPaymentAmount`(IN customerId int)
BEGIN
	DECLARE lastPaymentDate DATE;
	DECLARE lastPaymentAmount DOUBLE;
	
	SELECT MAX(DATE) FROM ACCOUNT_RECEIVABLE WHERE CUSTOMER_ID = customerId 
	AND DELETED = 0  AND TYPE = 'PAYMENT' INTO lastPaymentDate;

	 SELECT AMOUNT FROM ACCOUNT_RECEIVABLE WHERE CUSTOMER_ID = customerId 
	AND DELETED = 0 AND TYPE = 'PAYMENT' AND DATE = lastPaymentDate 
	INTO lastPaymentAmount;

	SELECT  lastPaymentAmount;
END

CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getLastPaymentDate`(IN customerId int)
BEGIN
	SELECT MAX(DATE) FROM ACCOUNT_RECEIVABLE WHERE CUSTOMER_ID = customerId 
	AND DELETED = 0 AND TYPE = 'PAYMENT';
END

CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getMaxAllowableAmount`(IN customerId int)
BEGIN
	SELECT MAX_AMOUNT FROM CUSTOMER_ACCT_SETTING 
	WHERE CUSTOMER_ID = customerId;
END

CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getPaymentTotAmt`(IN customerId int)
BEGIN
	SELECT SUM(AMOUNT) FROM ACCOUNT_RECEIVABLE WHERE CUSTOMER_ID = customerId 
	AND DELETED = 0 AND TYPE = 'PAYMENT';
END

CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getUserGroupIdByName`(IN name varchar(50))
BEGIN
	SELECT USER_GROUP_ID FROM USER_GROUP WHERE NAME = name;
END

CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getUserIdByUserName`(IN userName VARCHAR(30))
BEGIN
	SELECT USER_ID FROM USER WHERE USER_NAME = userName;
END

CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `isExistingUser`(IN userId int)
BEGIN
	SELECT USER_ID FROM  USER_LOGIN_STATUS WHERE USER_ID = userId;
END

CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `validateUser`(IN username varchar(50), IN password varchar(50))
BEGIN
	SELECT USER_NAME FROM USER
	WHERE USER_NAME LIKE '%username%'  
	AND PASSWORD LIKE '%SHA1 (password)%';
END

--
-- Get the unpaid account receivable total amount per company.
--
CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getUnpaidAcctReceivable`(IN companyId int)
BEGIN
	SELECT COALESCE(SUM(EARNED_INTEREST(AR.AMOUNT, CAS.INTEREST_RATE, NOW(), AR.INTEREST_DATE) + 
	AR.AMOUNT + AR.EARNED_INTEREST),0)  FROM (SELECT * FROM CUSTOMER WHERE DELETED = 0) C LEFT JOIN 
	(SELECT * FROM ACCOUNT_RECEIVABLE WHERE DELETED = 0 AND PAID = 0) AR ON AR.CUSTOMER_ID = C.CUSTOMER_ID 
	LEFT JOIN (SELECT CUSTOMER_ID, INTEREST_RATE FROM CUSTOMER_ACCT_SETTING) CAS ON CAS.CUSTOMER_ID = C.CUSTOMER_ID 
	WHERE C.COMPANY_ID = companyId ORDER BY C.NAME;
END

--
-- Get the monthly unpaid account receivable total amount per company.
--
CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getMonthlyUnpaidAcctReceivable`(IN companyId int,  IN month int, IN year int )
BEGIN
	DECLARE startDate DATE;
	DECLARE endDate DATE;
	DECLARE maxDay INTEGER;

	SELECT DAY(LAST_DAY(CONCAT(year,'-',month,'-01'))) INTO maxDay;

	SET startDate = CONCAT(year,'-',month,'-01');
	SET endDate = CONCAT(year,'-',month,'-',maxDay);

	SELECT COALESCE(SUM(EARNED_INTEREST(AR.AMOUNT, CAS.INTEREST_RATE, NOW(), AR.INTEREST_DATE) + 
	AR.AMOUNT + AR.EARNED_INTEREST),0)  FROM (SELECT * FROM CUSTOMER WHERE DELETED = 0) C LEFT JOIN 
	(SELECT * FROM ACCOUNT_RECEIVABLE WHERE DELETED = 0 AND PAID = 0) AR ON AR.CUSTOMER_ID = C.CUSTOMER_ID 
	LEFT JOIN (SELECT CUSTOMER_ID, INTEREST_RATE FROM CUSTOMER_ACCT_SETTING) CAS ON CAS.CUSTOMER_ID = C.CUSTOMER_ID 
	WHERE C.COMPANY_ID = companyId AND DATE BETWEEN startDate AND endDate ORDER BY C.NAME;
END

--
-- Get the yearly unpaid account receivable total amount per company.
--
CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getYearlyUnpaidAcctReceivable`(IN companyId int,  IN year int)
BEGIN
	SELECT COALESCE(SUM(EARNED_INTEREST(AR.AMOUNT, CAS.INTEREST_RATE, NOW(), AR.INTEREST_DATE) + 
	AR.AMOUNT + AR.EARNED_INTEREST),0)  FROM (SELECT * FROM CUSTOMER WHERE DELETED = 0) C LEFT JOIN 
	(SELECT * FROM ACCOUNT_RECEIVABLE WHERE DELETED = 0 AND PAID = 0) AR ON AR.CUSTOMER_ID = C.CUSTOMER_ID 
	LEFT JOIN (SELECT CUSTOMER_ID, INTEREST_RATE FROM CUSTOMER_ACCT_SETTING) CAS ON CAS.CUSTOMER_ID = C.CUSTOMER_ID 
	WHERE C.COMPANY_ID = companyId AND YEAR(DUE_DATE) = year ORDER BY C.NAME;
END

--
-- Get the unpaid account receivable total amount per company by date range.
--
CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getTotalUnpaidAcctReceivable`(IN companyId int,  IN startDate date, IN endDate date)
BEGIN

	IF (startDate IS NOT NULL) && (endDate IS NOT NULL) THEN
		SELECT COALESCE(SUM(EARNED_INTEREST(AR.AMOUNT, CAS.INTEREST_RATE, NOW(), AR.INTEREST_DATE) + 
		AR.AMOUNT + AR.EARNED_INTEREST),0)  FROM (SELECT * FROM CUSTOMER WHERE DELETED = 0) C LEFT JOIN 
		(SELECT * FROM ACCOUNT_RECEIVABLE WHERE DELETED = 0 AND PAID = 0) AR ON AR.CUSTOMER_ID = C.CUSTOMER_ID 
		LEFT JOIN (SELECT CUSTOMER_ID, INTEREST_RATE FROM CUSTOMER_ACCT_SETTING) CAS ON CAS.CUSTOMER_ID = C.CUSTOMER_ID 
		WHERE C.COMPANY_ID = companyId AND DATE BETWEEN startDate AND endDate ORDER BY C.NAME;
	ELSEIF (startDate IS NULL) THEN
		SELECT COALESCE(SUM(EARNED_INTEREST(AR.AMOUNT, CAS.INTEREST_RATE, NOW(), AR.INTEREST_DATE) + 
		AR.AMOUNT + AR.EARNED_INTEREST),0)  FROM (SELECT * FROM CUSTOMER WHERE DELETED = 0) C LEFT JOIN 
		(SELECT * FROM ACCOUNT_RECEIVABLE WHERE DELETED = 0 AND PAID = 0) AR ON AR.CUSTOMER_ID = C.CUSTOMER_ID 
		LEFT JOIN (SELECT CUSTOMER_ID, INTEREST_RATE FROM CUSTOMER_ACCT_SETTING) CAS ON CAS.CUSTOMER_ID = C.CUSTOMER_ID 
		WHERE C.COMPANY_ID = companyId AND DATE = endDate ORDER BY C.NAME;
	ELSEIF (endDate IS NULL) THEN
		SELECT COALESCE(SUM(EARNED_INTEREST(AR.AMOUNT, CAS.INTEREST_RATE, NOW(), AR.INTEREST_DATE) + 
		AR.AMOUNT + AR.EARNED_INTEREST),0)  FROM (SELECT * FROM CUSTOMER WHERE DELETED = 0) C LEFT JOIN 
		(SELECT * FROM ACCOUNT_RECEIVABLE WHERE DELETED = 0 AND PAID = 0) AR ON AR.CUSTOMER_ID = C.CUSTOMER_ID 
		LEFT JOIN (SELECT CUSTOMER_ID, INTEREST_RATE FROM CUSTOMER_ACCT_SETTING) CAS ON CAS.CUSTOMER_ID = C.CUSTOMER_ID 
		WHERE C.COMPANY_ID = companyId AND DATE = startDate ORDER BY C.NAME;
	END IF;
END


