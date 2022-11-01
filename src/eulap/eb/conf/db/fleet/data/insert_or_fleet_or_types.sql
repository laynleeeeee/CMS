
-- Description: Insert scripts to insert OR Types

INSERT INTO OR_TYPE VALUES (24, 'Fleet-Driver-Ref-Doc-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (25, 'Fleet-Incident-Ref-Doc-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO `OR_TYPE` (`OR_TYPE_ID`, `NAME`, `ACTIVE`, `CREATED_BY`, `CREATED_DATE`, `UPDATED_BY`, `UPDATED_DATE`)
VALUES ('26', 'Fleet-Insurance-Ref-Doc-Relationship', '1', '1', now(), '1', now());

INSERT INTO OR_TYPE VALUES (33, 'Fleet-Captain-Mdm-Ref-Doc-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (37, 'Fleet-Captain-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (38, 'Fleet-Mdm-Relationship', 1, 1, NOW(), 1, NOW());

INSERT INTO `OR_TYPE` (`OR_TYPE_ID`, `NAME`, `ACTIVE`, `CREATED_BY`, `CREATED_DATE`, `UPDATED_BY`, `UPDATED_DATE`)
VALUES ('34', 'Fleet-Manning-Requirement-Ref-Doc-Relationship', '1', '1', now(), '1', now());
INSERT INTO `OR_TYPE` (`OR_TYPE_ID`, `NAME`, `ACTIVE`, `CREATED_BY`, `CREATED_DATE`, `UPDATED_BY`, `UPDATED_DATE`)
VALUES ('35', 'Fleet-Manning-Requirement-Deck-Relationship', '1', '1', now(), '1', now());
INSERT INTO `OR_TYPE` (`OR_TYPE_ID`, `NAME`, `ACTIVE`, `CREATED_BY`, `CREATED_DATE`, `UPDATED_BY`, `UPDATED_DATE`)
VALUES ('36', 'Fleet-Manning-Requirement-Engine-Relationship', '1', '1', now(), '1', now());

INSERT INTO OR_TYPE VALUES (39, 'Fleet-Type-Company-Relationship', 1, 1, NOW(), 1, NOW());

INSERT INTO OR_TYPE VALUES (40, 'Fleet-Profile-Company-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (41, 'Fleet-Profile-Type-Relationship', 1, 1, NOW(), 1, NOW());

INSERT INTO OR_TYPE VALUES (42, 'Fleet-PMS-Ref-Doc-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (51, 'Fleet-Profile-Division-Relationship', 1, 1, NOW(), 1, NOW());

INSERT INTO OR_TYPE VALUES (53, 'Fleet-Tool-Ref-Doc-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (54, 'Fleet-Tool-Condition-Item-Relationship', 1, 1, NOW(), 1, NOW());

INSERT INTO OR_TYPE VALUES (69, 'Fleet-Dry-Dock-Ref-Doc-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES (70, 'Fleet-Voyage-Ref-Doc-Relationship', 1, 1, NOW(), 1, NOW());