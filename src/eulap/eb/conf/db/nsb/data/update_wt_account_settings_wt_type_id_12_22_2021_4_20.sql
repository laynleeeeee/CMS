-- Description: Update wt account wt type id values for all expanded withholding ;


UPDATE WT_ACCOUNT_SETTING SET WT_TYPE_ID = 1 WHERE CREDITABLE = 0;