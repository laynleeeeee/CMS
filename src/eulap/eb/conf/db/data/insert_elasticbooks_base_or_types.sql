

-- Description: Insert script for CMS base module OR Types.

SYSTEM echo 'Inserting base OR_TYPE';
INSERT INTO OR_TYPE VALUES (1, 'Parent-to-Child-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (9, 'Reference-Document-Relationship', 1, 1, now(), 1, now());
