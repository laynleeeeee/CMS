
-- Description: Create script for clean DB.

SYSTEM echo "====================================="
SYSTEM echo "==========ELASTICBOOKS BASE=========="
SYSTEM echo "====================================="
source ~/CMS/src/eulap/eb/conf/db/update/clean/consolidated_script_for_CMS_base.sql

SYSTEM echo "======================================"
SYSTEM echo "==========ACCOUNTING MODULES=========="
SYSTEM echo "======================================"
source ~/CMS/src/eulap/eb/conf/db/update/clean/consolidated_script_for_accounting.sql

SYSTEM echo "====================================="
SYSTEM echo "==========INVENTORY MODULES=========="
SYSTEM echo "====================================="
source ~/CMS/src/eulap/eb/conf/db/update/clean/consolidated_script_for_inventory.sql

SYSTEM echo "======================================"
SYSTEM echo "==========PROCESSING MODULES=========="
SYSTEM echo "======================================"
source ~/CMS/src/eulap/eb/conf/db/update/clean/consolidated_script_for_processing.sql

SYSTEM echo "=============================="
SYSTEM echo "==========HR MODULES=========="
SYSTEM echo "=============================="
source ~/CMS/src/eulap/eb/conf/db/update/clean/consolidated_script_for_hr.sql

SYSTEM echo "=================================="
SYSTEM echo "==========DIRECT PAYMENT=========="
SYSTEM echo "=================================="
source ~/CMS/src/eulap/eb/conf/db/directpayment/consolidated_direct_payment_script_06_03_2020_06_56.sql

SYSTEM echo "==================================================================";
SYSTEM echo "======================== FLEET MODULE ============================";
SYSTEM echo "==================================================================";
source ~/CMS/src/eulap/eb/conf/db/update/clean/consolidated_script_for_fleet.sql

SYSTEM echo "============================================================";
SYSTEM echo "============== REVENUE AND DISBURSEMENT ====================";
SYSTEM echo "============================================================";
source ~/CMS/src/eulap/eb/conf/db/update/clean/consolidated_script_for_revenue_and_disbursement.sql

SYSTEM echo "=========================================================================";
SYSTEM echo "======================= UPDATING ALL OF THE VIEWS =======================";
SYSTEM echo "=========================================================================";
source ~/CMS/src/eulap/eb/conf/db/view/consolidated_view_script.sql

SYSTEM echo "=========================================================================";
SYSTEM echo "============== UPDATING ALL OF THE STORED PROCEDURES ====================";
SYSTEM echo "=========================================================================";
source ~/CMS/src/eulap/eb/conf/db/sp/consolidated_stored_procedure_script.sql
