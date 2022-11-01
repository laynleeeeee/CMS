
-- Description : Consolidated script to add revenue and disbursement module

SET FOREIGN_KEY_CHECKS = 0;

SYSTEM echo "disabling fleet fishing"
source ~/CMS/src/eulap/eb/conf/db/update/update_fleet_category_inactive_fishing_12_17_2020_09_47.sql
SYSTEM echo "fleet-driver"
source ~/CMS/src/eulap/eb/conf/db/inv/driver.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_fleet_profile_add_driver_id_12_16_2020_17_02.sql

SYSTEM echo "adding serial item setup"
source ~/CMS/src/eulap/eb/conf/db/disbursement/data/insert_serial_item_setup_01_18_2021_11_35.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/update/update_serial_item_add_tax_type_id_12_07_2020_16_25.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/update/update_serial_item_add_discount_type_value_12_11_2020_08_25.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/update/update_item_add_columns_12_18_2020_08_49.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/update/update_item_disc_name_12_14_2020_11_37.sql

SYSTEM echo "executing revenue form scripts"
SYSTEM echo "ap_invoice_account"
source ~/CMS/src/eulap/eb/conf/db/disbursement/ap_invoice_account.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/data/insert_script_ap_invoice_account_12_04_2018_13_56.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/data/insert_account_01_15_2021_10_35.sql

SYSTEM echo "vat_account_setup"
source ~/CMS/src/eulap/eb/conf/db/accounting/vat_account_setup.sql
source ~/CMS/src/eulap/eb/conf/db/data/insert_vat_account_setup_06_25_2021_11_22.sql

SYSTEM echo "customer_type"
source ~/CMS/src/eulap/eb/conf/db/accounting/customer_type.sql
source ~/CMS/src/eulap/eb/conf/db/update/update_ar_customer_add_customer_type_project_11_26_2020_11_29.sql

source ~/CMS/src/eulap/eb/conf/db/revenue/division_project.sql

source ~/CMS/src/eulap/eb/conf/db/revenue/update/update_ar_line_setup_add_discount_and_amount_11_25_2020_10_15.sql

SYSTEM echo "sales_quotation"
source ~/CMS/src/eulap/eb/conf/db/revenue/sales_quotation.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/sales_quotation_item.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/sales_quotation_line.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/sales_quotation_equipment_line.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/sales_quotation_trucking_line.sql

SYSTEM echo "sales_order"
source ~/CMS/src/eulap/eb/conf/db/revenue/sales_order.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/sales_order_item.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/sales_order_line.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/sales_order_equimpment_line.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/sales_order_trucking_line.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/so_type.sql

SYSTEM echo "authority_to_withdraw"
source ~/CMS/src/eulap/eb/conf/db/revenue/authority_to_withdraw.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/authority_to_withdraw_item.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/authority_to_withdraw_line.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/update/update_authority_to_withdraw_and_driver_and_fleet_12_17_2020_09_52.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/update/update_authority_to_withdraw_set_remarks_to_ship_to_12_17_2020_14_20.sql

SYSTEM echo "updating customer_advance_payment"
source ~/CMS/src/eulap/eb/conf/db/revenue/customer_advance_payment.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/customer_advance_payment_item.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/cap_ar_line.sql

SYSTEM echo "delivery_receipt"
source ~/CMS/src/eulap/eb/conf/db/revenue/delivery_receipt.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/delivery_receipt_item.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/delivery_receipt_line.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/delivery_receipt_type.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/update/update_delivery_receipt_add_type_id_12_17_2020_16_34.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/update/update_delivery_receipt_add_sale_order_id_12_19_2020_11_41.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/data/insert_delivery_receipt_type_12_17_2020_16_22.sql

SYSTEM echo "ar_invoice"
source ~/CMS/src/eulap/eb/conf/db/revenue/ar_invoice.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/ar_invoice_item.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/ar_invoice_line.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/ar_invoice_transaction.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/ar_invoice_trucking_line.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/update/update_ar_other_charges_add_discount_type_id_12_21_2020_10_59.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/ar_invoice_type.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/data/insert_ar_invoice_type_01_05_2020_15_26.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/update/update_ar_invoice_add_type_id_01_05_2020_15_34.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/update/update_ar_invoice_add_dr_reference_ids_01_06_2020_16_00.sql

source ~/CMS/src/eulap/eb/conf/db/revenue/waybill_line.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/equipment_utilization_line.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/ar_invoice_equipment_line.sql
source ~/CMS/src/eulap/eb/conf/db/revenue/ar_invoice_trucking_line.sql

source ~/CMS/src/eulap/eb/conf/db/revenue/ar_receipt_advance_payment.sql

source ~/CMS/src/eulap/eb/conf/db/disbursement/update/update_ar_customer_add_default_withdrawal_default_advances_12_18_2020_19_02.sql

source ~/CMS/src/eulap/eb/conf/db/data/insert_ar_transaction_type_12_14_2020_08_28.sql
source ~/CMS/src/eulap/eb/conf/db/data/insert_revenue_and_disbursement_object_types.sql
source ~/CMS/src/eulap/eb/conf/db/data/insert_revenue_and_disbursement_or_types.sql
source ~/CMS/src/eulap/eb/conf/db/data/insert_form_status_12_14_2020_11_30.sql

source ~/CMS/src/bp/conf/db/update/consolidated_ar_receipt_update_script_08_02_2021_10_08.sql
source ~/CMS/src/bp/conf/db/update/consolidated_cap_update_scripts_07_22_2021_14_54.sql
source ~/CMS/src/bp/conf/db/update/consolidated_ar_invoice_update_script_08_06_2021_20_23.sql

SYSTEM echo "executing dibursement form scripts"
SYSTEM echo "work_order"
source ~/CMS/src/eulap/eb/conf/db/disbursement/work_order.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/work_order_instruction.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/work_order_item.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/work_order_line.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/work_order_purchased_item.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/work_order.sql

source ~/CMS/src/eulap/eb/conf/db/disbursement/project.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/ratio.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/requisition_classification.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/other_charges_line.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/requisition_type.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/requisition_form.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/requisition_form_item.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/purchase_requisition_item.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/jyei_withdrawal_slip.sql

source ~/CMS/src/eulap/eb/conf/db/disbursement/data/insert_default_requisition_classification_11_28_2018_11_27.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/data/insert_default_requisition_type_11_21_2018_13_33.sql

source ~/CMS/src/eulap/eb/conf/db/disbursement/consolidated_script_for_disbursement_module.sql
source ~/CMS/src/eulap/eb/conf/db/disbursement/update/update_requisition_form_add_work_order_id_07_01_2020_15_22.sql

SYSTEM echo "ap_invoice_goods"
source ~/CMS/src/eulap/eb/conf/db/accounting/ap_invoice_goods.sql

SYSTEM echo "supplier_advance_payment"
source ~/CMS/src/eulap/eb/conf/db/disbursement/supplier_advance_payment.sql

SYSTEM echo "applying updates for receiving report form"
source ~/CMS/src/bp/conf/db/update/consolidated_rr_update_script_07_07_2021_20_39.sql

SYSTEM echo "applying updates for AP payment form"
source ~/CMS/src/bp/conf/db/update/consolidated_ap_payment_update_script_07_07_2021_20_40.sql

SYSTEM echo "Petty Cash Voucher"
source ~/CMS/src/eulap/eb/conf/db/disbursement/petty_cash_voucher.sql

SET FOREIGN_KEY_CHECKS = 1;