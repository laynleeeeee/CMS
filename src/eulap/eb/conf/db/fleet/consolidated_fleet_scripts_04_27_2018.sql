
-- Description: Consolidated scripts for Fleet module.

SYSTEM echo "fleet_category.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/fleet_category.sql

SYSTEM echo "fleet_type.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/fleet_type.sql

SYSTEM echo "fleet_captain_mdm.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/fleet_captain_mdm.sql

SYSTEM echo "fleet_default_account.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/fleet_default_account.sql

SYSTEM echo "fleet_driver.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/fleet_driver.sql

SYSTEM echo "fleet_dry_dock.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/fleet_dry_dock.sql

SYSTEM echo "fleet_incident.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/fleet_incident.sql

SYSTEM echo "fleet_insurance_permit_renewal.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/fleet_insurance_permit_renewal.sql

SYSTEM echo "fleet_manning_requirement.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/fleet_manning_requirement.sql

SYSTEM echo "fleet_preventive_maintenance_schedule.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/fleet_preventive_maintenance_schedule.sql

SYSTEM echo "fleet_profile.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/fleet_profile.sql

SYSTEM echo "fleet_tool_condition.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/fleet_tool_condition.sql

SYSTEM echo "fleet_tool.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/fleet_tool.sql

SYSTEM echo "fleet_voyage.sql";
source ~/CMS/src/eulap/eb/conf/db/fleet/fleet_voyage.sql

SYSTEM echo "withdrawal_slip.sql";
source ~/CMS/src/eulap/eb/conf/db/inv/withdrawal_slip.sql

SYSTEM echo "withdrawal_slip_item.sql";
source ~/CMS/src/eulap/eb/conf/db/inv/withdrawal_slip_item.sql
