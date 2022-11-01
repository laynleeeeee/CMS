
-- Description : Insert script for invoice type for AP Invoice confidential

ALTER TABLE REINSERT INTO INVOICE_TYPE (INVOICE_TYPE_ID, EB_SL_KEY_ID, NAME, ACTIVE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE) 
VALUES ('37', '1', 'AP Invoice Confidential - Central', '1', '1', NOW(), '1', NOW()),
('38', '1', 'AP Invoice Confidential - NSB 3', '1', '1', NOW(), '1', NOW()),
('39', '1', 'AP Invoice Confidential - NSB 4', '1', '1', NOW(), '1', NOW()),
('40', '1', 'AP Invoice Confidential - NSB 5', '1', '1', NOW(), '1', NOW()),
('41', '1', 'AP Invoice Confidential - NSB 8', '1', '1', NOW(), '1', NOW()),
('42', '1', 'AP Invoice Confidential - NSB 8A', '1', '1', NOW(), '1', NOW());
