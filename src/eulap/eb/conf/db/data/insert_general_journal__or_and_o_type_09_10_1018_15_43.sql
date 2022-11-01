
-- Description: Insert script to insert OR Type and object type for journal Entry

ADD CONSTRAINT FK_GENERAL_LEDGER_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID);

ALTER TABLE GL_ENTRY ADD EB_OBJECT_ID int(10) unsigned DEFAULT NULL AFTER GL_ENTRY_ID,
ADD CONSTRAINT FK_GL_ENTRY_EB_OBJECT_ID FOREIGN KEY (EB_OBJECT_ID) REFERENCES EB_OBJECT (EB_OBJECT_ID);


INSERT INTO OBJECT_TYPE VALUES (130, 'GeneralLedger', 'GENERAL_LEDGER', 'eulap.eb.service.GeneralLedgerService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (140, 'GL_ENTRY', 'GL_ENTRY', 'eulap.eb.service.GeneralLedgerService', 1, 1, NOW(), 1, NOW());

INSERT INTO OR_TYPE VALUES (101, 'General-Ledger-AR-Line-Relationship', 1, 1, now(), 1, now());
