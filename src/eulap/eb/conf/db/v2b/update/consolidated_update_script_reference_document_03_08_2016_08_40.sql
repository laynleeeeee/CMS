
-- Description: Consolidated update script for reference document.

SYSTEM echo "reference_document.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/reference_document.sql

SYSTEM echo "Insert or type"
INSERT INTO OR_TYPE VALUES (9, 'Reference-Document-Relationship', 1, 1, now(), 1, now());

SYSTEM echo "Insert object type"
INSERT INTO OBJECT_TYPE VALUES (30, 'REFERENCE_DOCUMENT', 'REFERENCE_DOCUMENT', 1, 1, NOW(), 1, NOW());