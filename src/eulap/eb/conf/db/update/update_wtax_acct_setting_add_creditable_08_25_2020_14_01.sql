
-- Description	: Update script that will add CREDITABLE column in WT_ACCOUNT_SETTING

ALTER TABLE WT_ACCOUNT_SETTING ADD COLUMN CREDITABLE TINYINT(1) DEFAULT 0;