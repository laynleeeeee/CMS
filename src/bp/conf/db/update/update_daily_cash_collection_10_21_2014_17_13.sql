
-- Description: sql update script for DAILY CASH COLLECTION.

SYSTEM echo "UPDATING DAILY CASH COLLECTION";
source ~/CBS/src/eulap/eb/conf/db/data/sp/sp_get_daily_cash_collection.sql
SYSTEM echo "SUCCESSFULLY UPDATED DAILY CASH COLLECTION";