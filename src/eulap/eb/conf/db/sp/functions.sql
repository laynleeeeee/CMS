-- Function for computing interest:

CREATE DEFINER=`cmsadmin`@`%` FUNCTION `EARNED_INTEREST`(amount DOUBLE, interestRate DOUBLE, currentDate Date, dueDate Date) RETURNS double
BEGIN
		DECLARE earnedInterest DOUBLE;
		DECLARE multiplier DOUBLE;
		DECLARE dateDiff INT;
		SET multiplier = interestRate /100/365;
		IF dueDate >= currentDate THEN SET dateDiff = 0;
		ELSE SET dateDiff = DATEDIFF(currentDate,dueDate);
		END IF;
		SET earnedInterest = amount * multiplier * dateDiff;
		RETURN REPLACE(FORMAT(earnedInterest, 3), ',', '');
END

--
-- Function for getting the customer account status.
--
CREATE DEFINER=`cmsadmin`@`%` FUNCTION `CUSTOMER_STATUS`(customerId int,  totalReceivable float) RETURNS int(11)
BEGIN
	DECLARE maxAllowableAmount DOUBLE;
	DECLARE countPastDue INTEGER ;
	DECLARE countOverDue 	INTEGER; 

	SELECT MAX_AMOUNT FROM CUSTOMER_ACCT_SETTING WHERE CUSTOMER_ID = customerId INTO maxAllowableAmount;

	SELECT COUNT(DUE_DATE) FROM ACCOUNT_RECEIVABLE AR  INNER JOIN CUSTOMER C ON C.CUSTOMER_ID = AR.CUSTOMER_ID 
	WHERE AR.CUSTOMER_ID = customerId AND AR.PAID = 0 AND AR.DELETED = 0 AND DATEDIFF(NOW(), DUE_DATE) > 0 
	INTO countPastDue;

	SELECT COUNT(DUE_DATE) FROM ACCOUNT_RECEIVABLE AR  INNER JOIN CUSTOMER C ON C.CUSTOMER_ID = AR.CUSTOMER_ID 
	WHERE AR.CUSTOMER_ID = customerId AND AR.PAID = 0 AND AR.DELETED = 0 AND (DATEDIFF(NOW(), DUE_DATE) = 0) 
	INTO countOverDue;

	IF (countPastDue > 0) && (countOverDue > 0) && (maxAllowableAmount < totalReceivable)THEN
		RETURN 1;
	END IF ;
	IF (countPastDue > 0) &&  (countOverDue > 0) && (maxAllowableAmount >= totalReceivable) THEN
		RETURN 2;
	END IF;
	IF (countPastDue > 0) &&  (maxAllowableAmount < totalReceivable) THEN
		RETURN 3;
	END IF;
	IF (countPastDue > 0) &&  (maxAllowableAmount >= totalReceivable) THEN
		RETURN 4;
	END IF;
	IF (countOverDue > 0) &&  (maxAllowableAmount < totalReceivable) THEN
		RETURN 5;
	END IF;
	IF (countOverDue > 0) &&  (maxAllowableAmount >= totalReceivable) THEN
		RETURN 6;
	END IF;

	RETURN 0;
END

--
-- Function for getting the account payable status.
--
CREATE DEFINER=`cmsadmin`@`%` FUNCTION `PAYABLE_STATUS`(payableProfileId int) RETURNS int(11)
BEGIN
	DECLARE countPastDue INTEGER ;
	DECLARE countOverDue 	INTEGER; 

	SELECT COUNT(DUE_DATE) FROM ACCOUNT_PAYABLE AP  INNER JOIN PAYABLE_PROFILE PP ON 
	AP.PAYABLE_PROFILE_ID = PP.PAYABLE_PROFILE_ID WHERE PP.PAYABLE_PROFILE_ID = payableProfileId 
	AND AP.PAID = 0 AND AP.DELETED = 0 AND DATEDIFF(NOW(), DUE_DATE) > 0 INTO countPastDue;

	SELECT COUNT(DUE_DATE) FROM ACCOUNT_PAYABLE AP  INNER JOIN PAYABLE_PROFILE PP ON 
	AP.PAYABLE_PROFILE_ID = PP.PAYABLE_PROFILE_ID WHERE PP.PAYABLE_PROFILE_ID = payableProfileId 
	AND AP.PAID = 0 AND AP.DELETED = 0 AND DATEDIFF(NOW(), DUE_DATE) = 0 INTO countOverDue;

	IF (countPastDue > 0) && (countOverDue > 0) THEN
		RETURN 1;
	END IF ;
	IF (countPastDue > 0) THEN
		RETURN 2;
	END IF ;
	IF (countOverDue > 0) THEN
		RETURN 3;
	END IF ;

	RETURN 0;
END
