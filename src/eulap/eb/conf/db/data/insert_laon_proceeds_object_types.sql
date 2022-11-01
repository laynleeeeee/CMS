
-- Description	: Insert script to add new object type for loan proceeds module

INSERT INTO OBJECT_TYPE VALUES ('24011', 'LOAN_PROCEEDS', 'LOAN_PROCEEDS', 'eulap.eb.service.LoanProceedsService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES ('24012', 'LOAN_PROCEEDS_LINE', 'LOAN_PROCEEDS_LINE', 'eulap.eb.service.LoanProceedsService', 1, 1, NOW(), 1, NOW());
