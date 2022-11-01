
-- Description : Insert object to for fleet pms dry docking and voyages.

INSERT INTO OBJECT_TYPE VALUES (115, 'FLEET_DRY_DOCK', 'FLEET_DRY_DOCK', 'eulap.eb.service.FleetDryDockService', 1, 1, NOW(), 1, NOW());
INSERT INTO OBJECT_TYPE VALUES (116, 'FLEET_VOYAGE', 'FLEET_VOYAGE', 'eulap.eb.service.FleetVoyageService', 1, 1, NOW(), 1, NOW());