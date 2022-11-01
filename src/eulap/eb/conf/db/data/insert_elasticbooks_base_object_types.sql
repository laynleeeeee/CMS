
-- Description: Insert script for CMS base object types.

SYSTEM echo 'Inserting CMS base object types';

INSERT INTO OBJECT_TYPE VALUES (30, 'REFERENCE_DOCUMENT', 'REFERENCE_DOCUMENT','eulap.eb.service.ReferenceDocumentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (80, 'USER', 'USER', 'eulap.eb.service.UserService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (81, 'COMPANY', 'COMPANY', 'eulap.eb.service.CompanyService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (101, 'DIVISION', 'DIVISION', 'eulap.eb.service.DivisionService', 1, 1, NOW(), 1, NOW());
