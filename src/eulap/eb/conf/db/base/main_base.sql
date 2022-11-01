
-- Description: Script for the base tables. 
SET foreign_key_checks = 0;

SELECT 'CREATING COMPANY TABLE';
\. ~/CBS/src/eulap/eb/conf/db/base/eb_client_info.sql
\. ~/CBS/src/eulap/eb/conf/db/base/eb_sl_key.sql

SET foreign_key_checks = 1;
