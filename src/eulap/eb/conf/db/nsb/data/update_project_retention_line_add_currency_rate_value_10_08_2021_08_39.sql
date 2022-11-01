
-- Description	: Add currency rate value column to project retention line table.

ALTER TABLE PROJECT_RETENTION_LINE ADD COLUMN CURRENCY_RATE_VALUE double DEFAULT 0;