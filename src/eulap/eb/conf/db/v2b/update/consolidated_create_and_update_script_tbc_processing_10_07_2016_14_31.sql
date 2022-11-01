
-- Description: Consolidated update and create script for tbc as of October 07, 2016.

SYSTEM echo "update_product_line_menu_09_23_2016_15_40.sql"
source ~/eb-fa/src/eulap/eb/conf/db/update/update_product_line_menu_09_23_2016_15_40.sql

SYSTEM echo "pos_middleware_setting.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/pos_middleware_setting.sql

SYSTEM echo "consolidated_create_item_conversion_09_22_2016_11_09.sql"
source ~/eb-fa/src/eulap/eb/conf/db/v2b/update/consolidated_create_item_conversion_09_22_2016_11_09.sql

SYSTEM echo "consolidated_create_cs_processing_09_26_2016_21_13.sql"
source ~/eb-fa/src/eulap/eb/conf/db/v2b/update/consolidated_create_cs_processing_09_26_2016_21_13.sql

SYSTEM echo "sales_order.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/sales_order.sql

SYSTEM echo "wip_special_order.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/wip_special_order.sql

SYSTEM echo "wipso_finished_product.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/wipso_finished_product.sql

SYSTEM echo "wipso_material.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/wipso_material.sql

-- CREATE or UPDATE - VIEWS
SYSTEM echo "=======================================================================";
SYSTEM echo "=======================Updating all of the views=======================";
SYSTEM echo "=======================================================================";
source ~/eb-fa/src/eulap/eb/conf/db/view/consolidated_view_script.sql

-- CREATE or UPDATE - STORED PROCEDURES
SYSTEM echo "=======================================================================";
SYSTEM echo "==============Updating all of the stored procedures====================";
SYSTEM echo "=======================================================================";
source ~/eb-fa/src/eulap/eb/conf/db/sp/consolidated_stored_procedure_script.sql
