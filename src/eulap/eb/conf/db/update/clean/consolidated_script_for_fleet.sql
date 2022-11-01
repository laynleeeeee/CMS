
-- Description: Create script for fleet modules.

SYSTEM echo "==================================";
SYSTEM echo "==========FLEET MODULE==========";
SYSTEM echo "==================================";
source ~/CMS/src/eulap/eb/conf/db/fleet/consolidated_fleet_scripts_04_27_2018.sql

SYSTEM echo "======================================"
SYSTEM echo "==========FLEET DEFAULT DATA=========="
SYSTEM echo "======================================"
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_default_fleet_data.sql
