
-- Description: Consolidated script for salary type.

SYSTEM echo "Creating table SALARY_TYPE";
source ~/CMS/src/bp/conf/db/salary_type.sql

SYSTEM echo "Insert into OBJECT_TYPE SALARY_TYPE";
INSERT INTO OBJECT_TYPE VALUES (128, 'SALARY_TYPE', 'SALARY_TYPE', 'eulap.eb.service.SalaryTypeService', 1, 1, NOW(), 1, NOW());

INSERT INTO EB_OBJECT(OBJECT_TYPE_ID, CREATED_BY, CREATED_DATE) VALUES (128, 1, NOW());
INSERT INTO EB_OBJECT(OBJECT_TYPE_ID, CREATED_BY, CREATED_DATE) VALUES (128, 1, NOW());
INSERT INTO EB_OBJECT(OBJECT_TYPE_ID, CREATED_BY, CREATED_DATE) VALUES (128, 1, NOW());

INSERT INTO SALARY_TYPE VALUES (1, 'Semi-Monthly', 1,  (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 128 LIMIT 0, 1), 1, NOW(), 1, NOW());
INSERT INTO SALARY_TYPE VALUES (2, 'Daily', 1,  (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 128 LIMIT 1, 1), 1, NOW(), 1, NOW());
INSERT INTO SALARY_TYPE VALUES (3, 'Monthly', 0,  (SELECT EB_OBJECT_ID FROM EB_OBJECT WHERE OBJECT_TYPE_ID = 128 LIMIT 2, 1), 1, NOW(), 1, NOW());

ALTER TABLE EMPLOYEE_SALARY_DETAILS ADD COLUMN SALARY_TYPE_ID int(10) unsigned DEFAULT NULL AFTER EMPLOYEE_ID, 
ADD CONSTRAINT FK_ESD_SALARY_DETAIL_ID FOREIGN KEY (SALARY_TYPE_ID) REFERENCES SALARY_TYPE (SALARY_TYPE_ID); 

UPDATE EMPLOYEE_SALARY_DETAILS SET SALARY_TYPE_ID = 1 WHERE SALARY_TYPE_ID IS NULL;