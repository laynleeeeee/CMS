
-- Description	: Insert script that wil add serial item config to saved retail item

INSERT INTO SERIAL_ITEM_SETUP (ITEM_ID, SERIALIZED_ITEM)
SELECT ITEM_ID, 0 FROM ITEM;