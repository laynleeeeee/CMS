
-- Description: Consolidated script for employee requests.

SYSTEM echo "Request Type"
source ~/CMS/src/eulap/eb/conf/db/payroll/request_type.sql

SYSTEM echo "Employee Request"
source ~/CMS/src/eulap/eb/conf/db/payroll/employee_request.sql

SYSTEM echo "Overtime Detail"
source ~/CMS/src/eulap/eb/conf/db/payroll/overtime_detail.sql

SYSTEM echo "Leave  Detail"
source ~/CMS/src/eulap/eb/conf/db/payroll/leave_detail.sql