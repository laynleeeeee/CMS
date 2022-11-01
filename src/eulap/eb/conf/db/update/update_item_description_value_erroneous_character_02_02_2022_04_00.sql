
-- Description : Script to update erroneous characters in item description

UPDATE ITEM SET DESCRIPTION = REPLACE(DESCRIPTION,'”','"') WHERE DESCRIPTION like '%”%';