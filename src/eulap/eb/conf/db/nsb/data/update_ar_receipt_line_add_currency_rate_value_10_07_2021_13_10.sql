
-- Description	: Add currency rate value column to ar receipt line table.

ALTER TABLE AR_RECEIPT_LINE ADD COLUMN CURRENCY_RATE_VALUE double DEFAULT 0;