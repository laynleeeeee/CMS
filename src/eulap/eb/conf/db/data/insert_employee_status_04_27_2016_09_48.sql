-- Description: Insert script to add EMPLOYEE_STATUS.

INSERT INTO EMPLOYEE_STATUS
VALUES (1, 'Z', 'Zero Exemption without qualified dependent', 1, 1, now(), 1, now()),
(2, 'S/ME', 'Single/Married Employee w/o qualified dependent', 1, 1, now(), 1, now()),
(3, 'ME1/S1', 'Single/Married Employee with 1 qualified dependent', 1, 1, now(), 1, now()),
(4, 'ME2/S2', 'Single/Married Employee with 2 qualified dependent', 1, 1, now(), 1, now()),
(5, 'ME3/S3', 'Single/Married Employee with 3 qualified dependent', 1, 1, now(), 1, now()),
(6, 'ME4/S4', 'Single/Married Employee with 4 qualified dependent', 1, 1, now(), 1, now());