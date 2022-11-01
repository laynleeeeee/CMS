
-- Description: Insert scripts CMS fleet object types.

SYSTEM echo 'Inserting CMS fleet object types';

INSERT INTO OBJECT_TYPE VALUES (82, 'FLEET_TYPE', 'FLEET_TYPE', 'eulap.eb.service.FleetTypeService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (83, 'FLEET_PROFILE', 'FLEET_PROFILE', 'eulap.eb.service.FleetProfileService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (89, 'FLEET_DRIVER', 'FLEET_DRIVER', 'eulap.eb.service.FleetDriverService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (90, 'FLEET_INCIDENT', 'FLEET_INCIDENT', 'eulap.eb.service.FleetIncidentService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (91, 'FLEET_INSURANCE_PERMIT_RENEWAL', 'FLEET_INSURANCE_PERMIT_RENEWAL', 'eulap.eb.service.FleetTypeService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (93, 'FLEET_CAPTAIN_MDM', 'FLEET_CAPTAIN_MDM', 'eulap.eb.service.FleetCaptainMdmService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (94, 'FLEET_MANNING_REQUIREMENT', 'FLEET_MANNING_REQUIREMENT', 'eulap.eb.service.FleetManningRequirementService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (95, 'FLEET_PMS', 'FLEET_PMS', 'eulap.eb.service.FleetPmsService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (96, 'WITHDRAWAL_SLIP', 'WITHDRAWAL_SLIP', 'eulap.eb.service.WithdrawalSlipService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (97, 'WITHDRAWAL_SLIP_ITEM', 'WITHDRAWAL_SLIP_ITEM', 'eulap.eb.service.WithdrawalSlipService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (104, 'FLEET_TOOL_CONDITION', 'FLEET_TOOL_CONDITION', 'eulap.eb.service.FleetToolService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (115, 'FLEET_DRY_DOCK', 'FLEET_DRY_DOCK', 'eulap.eb.service.FleetDryDockService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (116, 'FLEET_VOYAGE', 'FLEET_VOYAGE', 'eulap.eb.service.FleetVoyageService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (133, 'FLEET_CATEGORY', 'FLEET_CATEGORY', 'eulap.eb.service.FleetTypeService', 1, 1, NOW(), 1, NOW());
