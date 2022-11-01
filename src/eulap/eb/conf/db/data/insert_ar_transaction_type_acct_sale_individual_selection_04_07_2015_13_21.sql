
-- Description: Insert script to create Account Sale - IS and Account Sale Return - IS type in AR_TRANSACTION_TYPE table.

INSERT INTO AR_TRANSACTION_TYPE VALUES (10, 1, 'Account Sale - IS', 1, 1, now(), 1, now());
INSERT INTO AR_TRANSACTION_TYPE VALUES (11, 1, 'Account Sale Return - IS', 1, 1, now(), 1, now());