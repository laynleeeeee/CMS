
-- Description	: Insert script to restore missing ap payment lines for ap payment 957.

INSERT INTO AP_PAYMENT_LINE (AP_PAYMENT_ID,AP_PAYMENT_LINE_TYPE_ID, PAID_AMOUNT, EB_OBJECT_ID, CURRENCY_RATE_VALUE, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE)
VALUES (957, 1, 5550,43751, 1, 7, '2022-02-07 10:24:04', 7,'2022-02-07 10:24:04'), (957, 1, 23686.61,43752, 1, 7, '2022-02-07 10:24:04', 7,'2022-02-07 10:24:04');