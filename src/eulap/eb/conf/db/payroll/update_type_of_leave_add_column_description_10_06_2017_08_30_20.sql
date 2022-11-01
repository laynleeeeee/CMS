
-- Description : Alter script that will add description column on TYPE_OF_LEAVE Table.

ALTER TABLE TYPE_OF_LEAVE ADD COLUMN DESCRIPTION TEXT DEFAULT NULL AFTER NAME;