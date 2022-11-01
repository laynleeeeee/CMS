
-- Description: Insert scripts for default data for eb-fa-3.01-cw branch

-- Insert Online User User Group
INSERT INTO USER_GROUP VALUES (2, 'ONLINE USER', 'ONLINE USER', 1, 1, NOW(), 1, NOW());

-- Insert Online User Position
INSERT INTO POSITION VALUES (2, 'ONLINE USER', 1, NOW(), 1, NOW(), 1);

-- Insert admin settings for 'Online User' User group
-- Default settings: Bank Account, Checkbook, Supplier, Supplier Account
INSERT INTO USER_GROUP_ACCESS_RIGHT VALUES
(2, 2, 184, 1, 1, NOW(), 1, NOW()),
(3, 2, 185, 1, 1, NOW(), 1, NOW()),
(4, 2, 176, 1, 1, NOW(), 1, NOW()),
(5, 2, 177, 1, 1, NOW(), 1, NOW());