DELIMITER ;;
DROP procedure if exists getUserIdByUserName;
CREATE DEFINER=`cmsadmin`@`%` PROCEDURE `getUserIdByUserName`(IN userName VARCHAR(30))
BEGIN
	SELECT USER_ID FROM USER WHERE USER_NAME = userName;
END ;;
DELIMITER ;