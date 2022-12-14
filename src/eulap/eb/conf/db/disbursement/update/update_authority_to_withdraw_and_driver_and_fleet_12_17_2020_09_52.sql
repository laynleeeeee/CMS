
-- Description	: Update authority to withdraw add driver, fleet profile, and ship to.

ALTER TABLE AUTHORITY_TO_WITHDRAW ADD COLUMN DRIVER_ID INT(10) UNSIGNED DEFAULT NULL AFTER AR_CUSTOMER_ACCOUNT_ID,
ADD CONSTRAINT FK_AUTHORITY_TO_WITHDRAW_DRIVER_ID FOREIGN KEY (DRIVER_ID)
REFERENCES DRIVER (DRIVER_ID);

ALTER TABLE AUTHORITY_TO_WITHDRAW ADD COLUMN FLEET_PROFILE_ID INT(10) UNSIGNED DEFAULT NULL AFTER DRIVER_ID,
ADD CONSTRAINT FK_AUTHORITY_TO_WITHDRAW_FLEET_PROFILE_ID FOREIGN KEY (FLEET_PROFILE_ID)
REFERENCES FLEET_PROFILE (FLEET_PROFILE_ID);

ALTER TABLE AUTHORITY_TO_WITHDRAW ADD COLUMN SHIP_TO TEXT AFTER FLEET_PROFILE_ID;