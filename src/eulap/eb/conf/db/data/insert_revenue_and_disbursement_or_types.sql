
-- Description	: Insert script to add new or types for revenue and disbursement

INSERT INTO OR_TYPE VALUES ('74', 'Purchase-Order-Receiving-Report-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES ('3000', 'Withdrawal-Slip-Serial-Item-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES ('3001', 'Withdrawal-Slip-Requisition-Form-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES ('3003', 'PR-PO-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES ('3010', 'PRI-POI-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES ('3011', 'PRI-POI-2-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES ('3012', 'RFI-PRI-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES ('12000', 'ATW-Serial-Item-Relationship', '1', '1', NOW(), '1', NOW());
INSERT INTO OR_TYPE VALUES ('12001', 'SO-Item-ATW-Item-Relationship', '1', '1', NOW(), '1', NOW());
INSERT INTO OR_TYPE VALUES ('12002', 'SQ-Item-SO-Item-Relationship', '1', '1', NOW(), '1', NOW());
INSERT INTO OR_TYPE VALUES ('12003', 'SO-Item-CAP-Item-Relationship', '1', '1', NOW(), '1', NOW());
INSERT INTO OR_TYPE VALUES ('12004', 'DR-Serial-Item-Relationship', '1', '1', NOW(), '1', NOW());
INSERT INTO OR_TYPE VALUES ('12005', 'ATW-Item-DR-Item-Relationship', '1', '1', NOW(), '1', NOW());
INSERT INTO OR_TYPE VALUES ('12006', 'ARI-Serial-Item-Relationship', '1', '1', NOW(), '1', NOW());
INSERT INTO OR_TYPE VALUES ('12007', 'DR-Item-ARI-Item-Relationship', '1', '1', NOW(), '1', NOW());
INSERT INTO OR_TYPE VALUES ('12008', 'SO-Line-WO-Line-Relationship', '1', '1', NOW(), '1', NOW());
INSERT INTO OR_TYPE VALUES ('12009', 'SO-Line-WB-Line-Relationship', '1', '1', NOW(), '1', NOW());
INSERT INTO OR_TYPE VALUES ('12010', 'EU-Child-Object-Relationship', '1', '1', NOW(), '1', NOW());
INSERT INTO OR_TYPE VALUES ('12011', 'WO-Serial-Item-Object-Relationship', '1', '1', NOW(), '1', NOW());
INSERT INTO OR_TYPE VALUES ('12012', 'DR-Service-Child-Object-Relationship', '1', '1', NOW(), '1', NOW());
INSERT INTO OR_TYPE VALUES ('12013', 'ARI-Service-Child-Object-Relationship', '1', '1', NOW(), '1', NOW());
INSERT INTO OR_TYPE VALUES ('24000', 'Adv-Payment-Object-Relationship', '1', '1', NOW(), '1', NOW());
INSERT INTO OR_TYPE VALUES ('24001', 'AP-Invoice-GS-Serial-Item-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES ('24002', 'AP-Invoice-GS-Receiving-Report-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES ('24003', 'AP-Invoice-GS-Child-Obj-Relationship', 1, 1, NOW(), 1, NOW());
INSERT INTO OR_TYPE VALUES ('24004', 'AP-Payment-Line-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES ('24005', 'AP-Invoice-GS-RTS-Obj-Relationship', 1, 1, now(), 1, now());
INSERT INTO OR_TYPE VALUES ('24006', 'AR-Receipt-Line-ARI-CAP-Obj-Relationship', 1, 1, now(), 1, now());
