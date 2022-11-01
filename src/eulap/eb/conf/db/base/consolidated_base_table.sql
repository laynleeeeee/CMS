
-- Description: Consolidated base script for CMS


SYSTEM echo "==================================================================";
SYSTEM echo "===================== CREATING BASE TABLE ========================";
SYSTEM echo "=================================================================="

SET FOREIGN_KEY_CHECKS = 0;

SYSTEM echo "Object to Object Relationships"
source ~/CMS/src/bp/conf/db/or_type.sql
source ~/CMS/src/bp/conf/db/object_type.sql
source ~/CMS/src/bp/conf/db/eb_object.sql
source ~/CMS/src/bp/conf/db/object_to_object.sql

SYSTEM echo "Company"
source ~/CMS/src/eulap/eb/conf/db/base/company.sql
source ~/CMS/src/eulap/eb/conf/db/base/company_product.sql
source ~/CMS/src/eulap/eb/conf/db/base/eb_client_info.sql
source ~/CMS/src/eulap/eb/conf/db/base/eb_sl_key.sql

SYSTEM echo "USER and Related tables"
source ~/CMS/src/eulap/eb/conf/db/base/user_group.sql
source ~/CMS/src/eulap/eb/conf/db/base/product_code.sql
source ~/CMS/src/eulap/eb/conf/db/base/module_code.sql
source ~/CMS/src/eulap/eb/conf/db/base/ug_m_access_right.sql
source ~/CMS/src/eulap/eb/conf/db/base/ug_mf_access_right.sql

source ~/CMS/src/eulap/eb/conf/db/base/position.sql
source ~/CMS/src/eulap/eb/conf/db/base/user.sql
source ~/CMS/src/eulap/eb/conf/db/base/user_login_status.sql
source ~/CMS/src/eulap/eb/conf/db/base/user_group_access_right.sql
source ~/CMS/src/eulap/eb/conf/db/base/user_company_head.sql
source ~/CMS/src/eulap/eb/conf/db/base/user_company.sql

SYSTEM echo "Division"
source ~/CMS/src/eulap/eb/conf/db/accounting/division.sql

SYSTEM echo "Form Workflow"
source ~/CMS/src/eulap/eb/conf/db/workflow/form_status.sql
source ~/CMS/src/eulap/eb/conf/db/workflow/form_workflow.sql
source ~/CMS/src/eulap/eb/conf/db/workflow/form_workflow_log.sql

SYSTEM echo "Reference Document"
source ~/CMS/src/eulap/eb/conf/db/inv/reference_document.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_reference_document_add_file_path_05_26_2021_10_28.sql

SYSTEM echo "Key Code"
source ~/CMS/src/eulap/eb/conf/db/accounting/key_code.sql

SET FOREIGN_KEY_CHECKS = 1;