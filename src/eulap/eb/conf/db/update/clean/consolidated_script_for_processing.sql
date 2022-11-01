
-- Description: Create script for processing modules.

SYSTEM echo "=============================="
SYSTEM echo "==========PROCESSING=========="
SYSTEM echo "=============================="

-- Bakeshop
SYSTEM echo "POS Middleware Settings"
source ~/CMS/src/eulap/eb/conf/db/inv/pos_middleware_setting.sql

SYSTEM echo "Product Line"
source ~/CMS/src/eulap/eb/conf/db/accounting/product_list.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/product_list_item.sql

SYSTEM echo "===================================="
SYSTEM echo "==========PROCESSING FORMS=========="
SYSTEM echo "===================================="

SYSTEM echo "Receiving Report - Raw Materials Item"
source ~/CMS/src/eulap/eb/conf/db/inv/r_receiving_report_rm_item.sql

SYSTEM echo "Cash Sales - Processing";
source ~/CMS/src/eulap/eb/conf/db/inv/csi_finished_product.sql
source ~/CMS/src/eulap/eb/conf/db/inv/csi_raw_material.sql

SYSTEM echo "Processing Report";
source ~/CMS/src/eulap/eb/conf/db/processing/processing_report_type.sql
source ~/CMS/src/eulap/eb/conf/db/processing/processing_report.sql
source ~/CMS/src/eulap/eb/conf/db/processing/pr_main_product.sql
source ~/CMS/src/eulap/eb/conf/db/processing/pr_by_product.sql
source ~/CMS/src/eulap/eb/conf/db/processing/pr_raw_materials_item.sql
source ~/CMS/src/eulap/eb/conf/db/processing/pr_other_materials_item.sql
source ~/CMS/src/eulap/eb/conf/db/processing/pr_other_charge.sql
source ~/CMS/src/eulap/eb/conf/db/processing/uom_conversion.sql

SYSTEM echo "Item Conversion";
source ~/CMS/src/eulap/eb/conf/db/inv/item_conversion.sql
source ~/CMS/src/eulap/eb/conf/db/inv/item_conversion_line.sql

SYSTEM echo "======================================";
SYSTEM echo "==========RICEMILLING MODULE==========";
SYSTEM echo "======================================";
source ~/CMS/src/eulap/eb/conf/db/update/clean/consolidated_script_for_ricemill_04_11_2018_14_17.sql

SYSTEM echo "==========================================="
SYSTEM echo "==========PROCESSING DEFAULT DATA=========="
SYSTEM echo "==========================================="
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_default_processing_data.sql
