
-- Description: Consolidated create script for version 2.02. All update script should be included in this file.

SYSTEM echo "item_buying_add_on.sql";
source ~/eb-fa/src/eulap/eb/conf/db/inv/item_buying_add_on.sql

SYSTEM echo "item_buying_discount.sql";
source ~/eb-fa/src/eulap/eb/conf/db/inv/item_buying_discount.sql

SYSTEM echo "item_buying_price.sql";
source ~/eb-fa/src/eulap/eb/conf/db/inv/item_buying_price.sql

SYSTEM echo "r_receiving_report_rm_item.sql";
source ~/eb-fa/src/eulap/eb/conf/db/inv/r_receiving_report_rm_item.sql

SYSTEM echo "ap_line_setup.sql";
source ~/eb-fa/src/eulap/eb/conf/db/accounting/ap_line_setup.sql

SYSTEM echo "ap_invoice_line.sql";
source ~/eb-fa/src/eulap/eb/conf/db/accounting/ap_invoice_line.sql

SYSTEM echo "processing_report.sql";
source ~/eb-fa/src/eulap/eb/conf/db/processing/processing_report.sql

SYSTEM echo "pr_raw_meterials_item.sql";
source ~/eb-fa/src/eulap/eb/conf/db/processing/pr_raw_materials_item.sql

SYSTEM echo "pr_other_meterials_item.sql";
source ~/eb-fa/src/eulap/eb/conf/db/processing/pr_other_materials_item.sql

SYSTEM echo "pr_other_charge.sql";
source ~/eb-fa/src/eulap/eb/conf/db/processing/pr_other_charge.sql

SYSTEM echo "pr_main_product.sql";
source ~/eb-fa/src/eulap/eb/conf/db/processing/pr_main_product.sql

SYSTEM echo "pr_by_product.sql";
source ~/eb-fa/src/eulap/eb/conf/db/processing/pr_by_product.sql

SYSTEM echo "transfer_receipt_type.sql";
source ~/eb-fa/src/eulap/eb/conf/db/inv/transfer_receipt_type.sql

SYSTEM echo "supplier_account_setting.sql";
source ~/eb-fa/src/eulap/eb/conf/db/accounting/supplier_account_setting.sql

SYSTEM echo "create_item_add_on_type_05_19_2015_12_17.sql";
source ~//eb-fa/src/eulap/eb/conf/db/v2b/create/create_item_add_on_type_05_19_2015_12_17.sql

SYSTEM echo "item_volume_conversion.sql";
source ~/eb-fa/src/eulap/eb/conf/db/accounting/item_volume_conversion.sql

SYSTEM echo "uom_conversion.sql";
source ~/eb-fa/src/eulap/eb/conf/db/processing/uom_conversion.sql

SYSTEM echo "product_list.sql";
source ~/eb-fa/src/eulap/eb/conf/db/accounting/product_list.sql

SYSTEM echo "product_list_item.sql";
source ~/eb-fa/src/eulap/eb/conf/db/accounting/product_list_item.sql

SYSTEM echo "customer_type.sql";
source ~/eb-fa/src/eulap/eb/conf/db/accounting/customer_type.sql

SYSTEM echo "reference_document.sql"
source ~/eb-fa/src/eulap/eb/conf/db/inv/reference_document.sql

SYSTEM echo "checkbook_template.sql";
source ~/eb-fa/src/eulap/eb/conf/db/accounting/checkbook_template.sql

SYSTEM echo "employee_status.sql";
source ~/eb-fa/src/eulap/eb/conf/db/payroll/employee_status.sql

SYSTEM echo "employee_shift_working_day.sql";
source ~/eb-fa/src/eulap/eb/conf/db/payroll/employee_shift_working_day.sql

