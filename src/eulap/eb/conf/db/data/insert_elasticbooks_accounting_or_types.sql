

-- Description: Insert script for accounting module OR Types.

SYSTEM echo 'Inserting accounting OR_TYPE';

INSERT INTO OR_TYPE VALUES (99, 'Direct-Payment-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (101, 'General-Ledger-AR-Line-Relationship', 1, 1, now(), 1, now());
