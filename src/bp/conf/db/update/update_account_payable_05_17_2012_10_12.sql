

--
-- Function for getting the account payable status.
--
delimiter //
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