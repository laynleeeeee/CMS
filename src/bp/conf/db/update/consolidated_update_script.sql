
-- Description: Consolidated update script.

SYSTEM echo "CREATE SCRIPT";
SYSTEM echo "or_type.sql";
source ~/CMS/src/bp/conf/db/or_type.sql

SYSTEM echo "object_type.sql";
source ~/CMS/src/bp/conf/db/object_type.sql

SYSTEM echo "eb_object.sql";
source ~/CMS/src/bp/conf/db/eb_object.sql

SYSTEM echo "object_to_object.sql";
source ~/CMS/src/bp/conf/db/object_to_object.sql

SYSTEM echo "INSERT SCRIPT";
SYSTEM echo "insert_object_type.sql";
source ~/CMS/src/bp/conf/db/data/insert_object_type.sql

SYSTEM echo "insert_or_type.sql";
source ~/CMS/src/bp/conf/db/data/insert_or_type.sql