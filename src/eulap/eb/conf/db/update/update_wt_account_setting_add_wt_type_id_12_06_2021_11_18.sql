
-- Description	: Update script to add WT_TYPE_ID to WT_ACCOUNT_SETTING

ALTER TABLE WT_ACCOUNT_SETTING ADD COLUMN WT_TYPE_ID INT(10) UNSIGNED AFTER BIR_ATC_ID;