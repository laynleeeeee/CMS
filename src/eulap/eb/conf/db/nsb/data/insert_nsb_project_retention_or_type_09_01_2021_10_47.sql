
-- Description	: Insert script that wil add the project retention or type

INSERT INTO OR_TYPE VALUES ('24007', 'Project-Retention-Child-Obj-Relationship', 1, 1, now(), 1, now());
INSERT INTO OBJECT_TYPE VALUES (24009, 'PROJECT_RETENTION', 'PROJECT_RETENTION','eulap.eb.service.ProjectRetentionService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (24010, 'PROJECT_RETENTION_LINE', 'PROJECT_RETENTION_LINE','eulap.eb.service.ProjectRetentionService', 1, 1, NOW(), 1, NOW());