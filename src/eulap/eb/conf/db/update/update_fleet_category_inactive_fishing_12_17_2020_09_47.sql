
-- Description	: Update fleet category set fishing to inactive and rename "CONSTRUCTION" to "TRUCKING".

UPDATE `FLEET_CATEGORY` SET `ACTIVE`='0' WHERE `FLEET_CATEGORY_ID`='2';
UPDATE `FLEET_CATEGORY` SET `NAME`='TRUCKING' WHERE `FLEET_CATEGORY_ID`='1';