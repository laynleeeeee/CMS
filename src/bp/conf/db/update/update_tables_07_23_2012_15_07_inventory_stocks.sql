
-- Update for INVENTORY_STOCK table.
-- Added UNIT_PRICE column
ALTER TABLE `CBS`.`INVENTORY_STOCK` ADD COLUMN `UNIT_PRICE` DECIMAL(10,2) NOT NULL  AFTER `QUANTITY` ;