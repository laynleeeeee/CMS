
-- Description: Consolidate script to implement changes/updates on ricemilling module

SYSTEM echo "Executing updates for ricemilling module";
SYSTEM echo "item_bag_quantity";
source ~/CMS/src/eulap/eb/conf/db/inv/item_bag_quantity.sql
SYSTEM echo "rri_bag_quantity";
source ~/CMS/src/eulap/eb/conf/db/inv/rri_bag_quantity.sql
SYSTEM echo "rri_bag_discount";
source ~/CMS/src/eulap/eb/conf/db/inv/rri_bag_discount.sql
