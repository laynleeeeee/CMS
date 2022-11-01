
-- Description	: Update authority to withdraw set remarks data to ship to.

UPDATE AUTHORITY_TO_WITHDRAW ATW SET SHIP_TO = REMARKS;
UPDATE AUTHORITY_TO_WITHDRAW ATW SET REMARKS = '';