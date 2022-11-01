
-- Description	: Add currency rate value column to ap payment line.

ALTER TABLE AP_PAYMENT_LINE ADD CURRENCY_RATE_VALUE double DEFAULT 0;