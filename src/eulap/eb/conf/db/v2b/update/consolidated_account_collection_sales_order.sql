
-- Description: Scripts for sales order update

SYSTEM echo "Inserting Ar Transaction Type for Sales Order."
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_ar_transaction_type_sales_order_12_14_2016_5_20.sql

SYSTEM echo "Migrating saved data from SALES_ORDER Table to AR_TRANSACTION Table."
source ~/eb-fa/src/eulap/eb/conf/db/v2b/update/insert_ar_transaction_convert_sales_order_12_14_16_05_44.sql

SYSTEM echo "Drop Foreign Key from sales order of WIP_SPECIAL_ORDER and add Foreign Key for AR_TRANSACTION."
source ~/eb-fa/src/eulap/eb/conf/db/update/update_wip_so_alter_change_so_fk_art_fk_12_20_16_14_59.sql

SYSTEM echo "DROP SALES_ORDER Table"
source ~/eb-fa/src/eulap/eb/conf/db/v2b/update/drop_sales_order_12_14_16_05_50.sql