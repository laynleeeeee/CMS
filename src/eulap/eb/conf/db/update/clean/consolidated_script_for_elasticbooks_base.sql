
-- Description: Create script for CMS base.

source ~/CMS/src/eulap/eb/conf/db/base/consolidated_base_table.sql

SYSTEM echo "INSERTING BASE TABLE DATA";
source ~/CMS/src/eulap/eb/conf/db/data/insert_CMS_default_base_data.sql
