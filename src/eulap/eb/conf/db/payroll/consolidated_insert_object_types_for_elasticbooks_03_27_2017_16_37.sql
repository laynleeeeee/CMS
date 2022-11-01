
-- Description: Append all object types here.

-- Employee Leave Credit
INSERT INTO OBJECT_TYPE VALUES (67, 'EMPLOYEE_LEAVE_CREDIT', 'EMPLOYEE_LEAVE_CREDIT','eulap.eb.service.EmployeeLeaveCreditService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (68, 'LEAVES', 'LEAVES','eulap.eb.service.EmployeeLeaveCreditService', 1, 1, NOW(), 1, NOW());

-- Employee Request
INSERT INTO OBJECT_TYPE VALUES (66, 'REQUEST_FOR_LEAVE', 'REQUEST_FOR_LEAVE','eulap.eb.service.EmployeeRequestService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (71, 'REQUEST_FOR_OVERTIME', 'REQUEST_FOR_OVERTIME','eulap.eb.service.EmployeeRequestService', 1, 1, NOW(), 1, NOW());

-- Form Deduction
INSERT INTO OBJECT_TYPE VALUES (69, 'FORM_DEDUCTION', 'FORM_DEDUCTION','eulap.eb.service.FormDeductionService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (70, 'FORM_DEDUCTION_LINE', 'FORM_DEDUCTION_LINE','eulap.eb.service.FormDeductionService', 1, 1, NOW(), 1, NOW());

-- Time Sheet
INSERT INTO OBJECT_TYPE VALUES (73, 'TIME_SHEET', 'TIME_SHEET','eulap.eb.service.TimeSheetService', 1, 1, NOW(), 1, NOW());
