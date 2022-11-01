
-- Description: Consolidated insert scripts for CMS HR default data.

SYSTEM echo "INSERT FLEET OBJECT TYPES"
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_fleet_object_types.sql

SYSTEM echo "INSERT FLEET OR TYPES"
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_fleet_or_types.sql

SYSTEM echo "insert_default_fleet_category.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/data/insert_default_fleet_category.sql

SYSTEM echo "insert_fleet_default_account.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/data/insert_fleet_default_account.sql
