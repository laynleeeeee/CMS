
-- Description: Add MENU in PRODUCT_LINE table

ALTER TABLE PRODUCT_LINE ADD MENU tinyint(1) DEFAULT 0 AFTER MAIN_ITEM_ID;