
-- Description	: Insert script that wil add default division for NSB

UPDATE DIVISION SET NAME='CENTRAL', DESCRIPTION='CENTRAL' WHERE DIVISION_ID='1';

INSERT INTO EB_OBJECT (OBJECT_TYPE_ID, CREATED_BY, CREATED_DATE) VALUES (101, 1, NOW()), (101, 1, NOW()),
(101, 1, NOW()), (101, 1, NOW()), (101, 1, NOW());

INSERT INTO DIVISION (DIVISION_ID, EB_OBJECT_ID, NUMBER, NAME, DESCRIPTION, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, EB_SL_KEY_ID)
VALUES ('2', (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 101 LIMIT 1, 1), '002', 'NSB 3', 'NSB 3', '1', '1', NOW(), '1', NOW(), '1'),
('3', (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 101 LIMIT 2, 1), '003', 'NSB 4', 'NSB 4', '1', '1', NOW(), '1', NOW(), '1'),
('4', (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 101 LIMIT 3, 1), '004', 'NSB 5', 'NSB 5', '1', '1', NOW(), '1', NOW(), '1'),
('5', (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 101 LIMIT 4, 1), '005', 'NSB 8', 'NSB 8', '1', '1', NOW(), '1', NOW(), '1'),
('6', (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 101 LIMIT 5, 1), '006', 'NSB 8A', 'NSB 8A', '1', '1', NOW(), '1', NOW(), '1');