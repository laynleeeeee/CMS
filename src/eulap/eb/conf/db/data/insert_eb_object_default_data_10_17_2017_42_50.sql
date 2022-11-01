
-- Description: Insert scripts ebObject for base default data.

-- EB_OBJECT

SYSTEM echo 'Inserting EB_OBJECT';
INSERT INTO EB_OBJECT VALUES
(1, 81, 1, NOW()),
(2, 80, 1, NOW()),
(3, 101, 1, NOW());

SYSTEM echo 'Inserting COMPANY';
INSERT INTO COMPANY VALUES (1, 1, '111', 'EULAP', 'Koronadal City', NULL, NULL, NULL, 1, NULL, 1, NOW(), 1, NOW(), 1, 'DS');

SYSTEM echo 'Inserting USER';
INSERT INTO USER VALUES (1, 'admin', SHA1('admin12354'), 'Admin', 'Admin', 'Admin', '1900-01-01', '123456789', 'info@admin.com', 'Koronadal City', 1, NULL, 1, 2, NULL, NOW(), NULL, NOW(), 1, 1 );

-- DIVISION
SYSTEM echo 'Inserting DIVISION';
INSERT INTO `DIVISION` VALUES (1,NULL,3,'001','Office dump','Office dump',1,1,NOW(),1,NOW(),1);