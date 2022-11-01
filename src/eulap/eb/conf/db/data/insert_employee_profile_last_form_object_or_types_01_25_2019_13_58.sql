
-- Description	: Insert script object types and OR types for employee profile last form tables

INSERT INTO OBJECT_TYPE VALUES (85, 'EMPLOYEE_EMERGENCY_CONTACT', 'EMPLOYEE_EMERGENCY_CONTACT', 'eulap.eb.service.EmployeeProfileService', 1, 1, now(), 1, now());
INSERT INTO OBJECT_TYPE VALUES (86, 'EMPLOYEE_LICENSE_CERTIFICATE', 'EMPLOYEE_LICENSE_CERTIFICATE', 'eulap.eb.service.EmployeeProfileService', 1, 1, now(), 1, now());
INSERT INTO OBJECT_TYPE VALUES (87, 'EMPLOYEE_SEMINAR_ATTENDED', 'EMPLOYEE_SEMINAR_ATTENDED', 'eulap.eb.service.EmployeeProfileService', 1, 1, now(), 1, now());
INSERT INTO OBJECT_TYPE VALUES (113, 'EMPLOYEE_NATIONAL_COMPETENCY', 'EMPLOYEE_NATIONAL_COMPETENCY', 'eulap.eb.service.EmployeeProfileService', 1, 1, now(), 1, now());

INSERT INTO OR_TYPE VALUES (31, 'Employee-Emergency-Contact-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (29, 'Employee-License-Cert-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (30, 'Employee-Seminar-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES (65, 'Employee-Nat-Comp-Relationship', 1, 1, now(), 1, now());