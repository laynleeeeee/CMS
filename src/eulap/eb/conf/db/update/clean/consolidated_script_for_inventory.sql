
-- Description: Create script for inventory modules.

SYSTEM echo "============================="
SYSTEM echo "==========INVENTORY=========="
SYSTEM echo "============================="

SYSTEM echo "Stock Adjustment Type"
source ~/CMS/src/eulap/eb/conf/db/inv/stock_adjustment_type.sql

SYSTEM echo "Item Category"
source ~/CMS/src/eulap/eb/conf/db/inv/item_category.sql
source ~/CMS/src/eulap/eb/conf/db/inv/item_category_account_setup.sql

SYSTEM echo "Unit of Measurement"
source ~/CMS/src/eulap/eb/conf/db/inv/unit_measurement.sql

SYSTEM echo "Warehouse"
source ~/CMS/src/eulap/eb/conf/db/inv/warehouse.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_warehouse_add_parent_id_04_05_2021_16_41.sql

SYSTEM echo "Item"
source ~/CMS/src/eulap/eb/conf/db/inv/item.sql
source ~/CMS/src/eulap/eb/conf/db/inv/item_srp.sql
source ~/CMS/src/eulap/eb/conf/db/inv/item_discount_type.sql
source ~/CMS/src/eulap/eb/conf/db/inv/item_discount.sql
source ~/CMS/src/eulap/eb/conf/db/inv/item_add_on_type.sql
source ~/CMS/src/eulap/eb/conf/db/inv/item_add_on.sql
source ~/CMS/src/eulap/eb/conf/db/inv/item_buying_price.sql
source ~/CMS/src/eulap/eb/conf/db/inv/item_buying_discount.sql
source ~/CMS/src/eulap/eb/conf/db/inv/item_buying_add_on.sql
source ~/CMS/src/eulap/eb/conf/db/v2b/update/consolidated_create_item_vat_type.sql

SYSTEM echo "Serial Item"
source ~/CMS/src/eulap/eb/conf/db/inv/serial_item.sql
source ~/CMS/src/eulap/eb/conf/db/inv/serial_item_setup.sql

SYSTEM echo "Inventory Account"
source ~/CMS/src/eulap/eb/conf/db/inv/inventory_account.sql

SYSTEM echo "==================================="
SYSTEM echo "==========INVENTORY FORMS=========="
SYSTEM echo "==================================="

SYSTEM echo "Account Sales Order/Account Sales Return"
source ~/CMS/src/eulap/eb/conf/db/inv/account_sale_item.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_account_sale_ar_line_add_tax_amount_07_06_2020_06_37.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_account_sale_item_add_tax_value_07_06_2020_06_36.sql
source ~/CMS/src/eulap/eb/conf/db/inv/account_sales.sql
source ~/CMS/src/eulap/eb/conf/db/inv/account_sales_po_item.sql

SYSTEM echo "Cash Sales Order"
source ~/CMS/src/eulap/eb/conf/db/inv/cash_sale_type.sql
source ~/CMS/src/eulap/eb/conf/db/inv/cash_sale.sql
source ~/CMS/src/eulap/eb/conf/db/inv/cash_sale_item.sql
source ~/CMS/src/eulap/eb/conf/db/inv/cash_sale_ar_line.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_cash_sales_add_withholding_tax_08_12_2020_09_06.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_cash_sale_item_add_tax_value_07_01_2020_10_57.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_cash_sale_ar_line_add_tax_amount_07_02_2020_09_00.sql

SYSTEM echo "Cash Sales Return"
source ~/CMS/src/eulap/eb/conf/db/inv/cash_sale_return.sql
source ~/CMS/src/eulap/eb/conf/db/inv/cash_sale_return_item.sql
source ~/CMS/src/eulap/eb/conf/db/inv/cash_sale_return_ar_line.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_cash_sales_return_add_withholding_tax_08_12_2020_11_12.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_cash_sale_return_item_add_tax_value_07_07_2020_14_19.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_cash_sale_return_ar_line_add_tax_value_07_07_2020_14_24.sql

SYSTEM echo "Customer Advance Payment"
source ~/CMS/src/eulap/eb/conf/db/inv/customer_advance_payment_type.sql
source ~/CMS/src/eulap/eb/conf/db/inv/customer_advance_payment.sql
source ~/CMS/src/eulap/eb/conf/db/inv/customer_advance_payment_item.sql
source ~/CMS/src/eulap/eb/conf/db/inv/cap_ar_line.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_customer_advance_payment_add_withholding_tax_08_13_2020_13_09.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_cap_item_add_tax_type_id_07_10_2020_09_52.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_cap_ar_line_add_tax_type_id_07_23_2020_11_59.sql

SYSTEM echo "Paid in Advance Delivery and PIAD"
source ~/CMS/src/eulap/eb/conf/db/inv/cap_delivery.sql
source ~/CMS/src/eulap/eb/conf/db/inv/cap_delivery_item.sql
source ~/CMS/src/eulap/eb/conf/db/inv/cap_delivery_ar_line.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_cap_delivery_add_withholding_tax_08_25_2020_15_46.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_piad_item_add_tax_type_id_07_10_2020_09_53.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_cap_delivery_ar_line_add_tax_type_id_07_23_2020_12_09.sql

-- Paid in Advance - AS
source ~/CMS/src/eulap/eb/conf/db/inv/cap_delivery_transaction.sql

SYSTEM echo "Purchase Order"
source ~/CMS/src/eulap/eb/conf/db/inv/r_purchase_order.sql
source ~/CMS/src/eulap/eb/conf/db/inv/r_purchase_order_item.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_po_item_add_tax_type_id_06_09_2021_14_03.sql
source ~/CMS/src/eulap/eb/conf/db/inv/purchase_order_line.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_po_line_add_tax_type_id_06_09_2021_14_4.sql

SYSTEM echo "Receiving Report"
source ~/CMS/src/eulap/eb/conf/db/inv/r_receiving_report.sql
source ~/CMS/src/eulap/eb/conf/db/inv/r_receiving_report_item.sql
source ~/CMS/src/eulap/eb/conf/db/accounting/ap_invoice_line.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_receiving_report_item_add_tax_value_07_01_2020_08_44.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_ap_invoice_line_add_tax_type_id_06_29_2020_11_18.sql

SYSTEM echo "Repacking"
source ~/CMS/src/eulap/eb/conf/db/inv/repacking.sql
source ~/CMS/src/eulap/eb/conf/db/inv/repacking_item.sql

SYSTEM echo "Return To Supplier"
source ~/CMS/src/eulap/eb/conf/db/inv/r_return_to_supplier.sql
source ~/CMS/src/eulap/eb/conf/db/inv/r_return_to_supplier_item.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_rts_item_add_tax_type_id_07_10_2020_09_32.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_rts_item_add_discount_type_value_07_17_2021_11_30.sql

SYSTEM echo "Stock Adjustment Classification";
source ~/CMS/src/eulap/eb/conf/db/inv/stock_adjustment_classification.sql

SYSTEM echo "Stock Adjustment - IN/OUT"
source ~/CMS/src/eulap/eb/conf/db/inv/stock_adjustment.sql
source ~/CMS/src/eulap/eb/conf/db/inv/stock_adjustment_item.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_stock_adjustment_add_tax_type_07_05_2021_07_55.sql

SYSTEM echo "Transfer Receipt"
source ~/CMS/src/eulap/eb/conf/db/inv/transfer_receipt_type.sql
source ~/CMS/src/eulap/eb/conf/db/inv/r_transfer_receipt.sql
source ~/CMS/src/eulap/eb/conf/db/inv/r_transfer_receipt_item.sql

SYSTEM echo "Repacking - Item Conversion"
source ~/CMS/src/eulap/eb/conf/db/inv/repacking_type.sql
source ~/CMS/src/eulap/eb/conf/db/inv/repacking_raw_material.sql
source ~/CMS/src/eulap/eb/conf/db/inv/repacking_finished_good.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_repacking_add_repacking_type_05_15_2020_10_06.sql

SYSTEM echo "=========================================="
SYSTEM echo "==========INVENTORY DEFAULT DATA=========="
SYSTEM echo "=========================================="
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_default_inventory_data.sql
