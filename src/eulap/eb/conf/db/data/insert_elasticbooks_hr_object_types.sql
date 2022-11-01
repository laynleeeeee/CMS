
-- Description: Insert scripts CMS hr object types.

SYSTEM echo 'Inserting CMS hr object types';

INSERT INTO OBJECT_TYPE VALUES (31, 'PAYROLL', 'PAYROLL', 'eulap.eb.service.payroll.PayrollService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (66, 'REQUEST_FOR_LEAVE', 'REQUEST_FOR_LEAVE','eulap.eb.service.EmployeeRequestService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (67, 'EMPLOYEE_LEAVE_CREDIT', 'EMPLOYEE_LEAVE_CREDIT','eulap.eb.service.EmployeeLeaveCreditService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (68, 'LEAVES', 'LEAVES','eulap.eb.service.EmployeeLeaveCreditService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (69, 'FORM_DEDUCTION', 'FORM_DEDUCTION','eulap.eb.service.FormDeductionService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (70, 'FORM_DEDUCTION_LINE', 'FORM_DEDUCTION_LINE','eulap.eb.service.FormDeductionService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (71, 'REQUEST_FOR_OVERTIME', 'REQUEST_FOR_OVERTIME','eulap.eb.service.EmployeeRequestService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (72, 'PERSONNEL_ACTION_NOTICE', 'PERSONNEL_ACTION_NOTICE','eulap.eb.service.PersonnelActionNoticeService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (73, 'TIME_SHEET', 'TIME_SHEET','eulap.eb.service.TimeSheetService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (102, 'EMPLOYEE_DEDUCTION', 'EMPLOYEE_DEDUCTION', 'eulap.eb.service.PayrollService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (126, 'EMPLOYEE_DOCUMENT', 'EMPLOYEE_DOCUMENT', 'eulap.eb.service.EmployeeDocumentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (128, 'SALARY_TYPE', 'SALARY_TYPE', 'eulap.eb.service.SalaryTypeService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (139, 'EMPLOYEE_PROFILE', 'EMPLOYEE_PROFILE', 'eulap.eb.service.EmployeeProfileService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (152, 'PAYROLL_EMPLOYEE_TIMESHEET', 'PAYROLL_EMPLOYEE_TIMESHEET', 'eulap.eb.service.TimeSheetService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (153, 'PAYROLL_EMPLOYEE_SALARY', 'PAYROLL_EMPLOYEE_SALARY', 'eulap.eb.service.PayrollService', 1, 1, NOW(), 1, NOW());
