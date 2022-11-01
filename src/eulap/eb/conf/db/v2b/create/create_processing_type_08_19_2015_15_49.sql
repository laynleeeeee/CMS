
-- Description: consolidated create script to create the PROCESSING_REPORT_TYPE and update the PROCESSING_REPORT table.

SYSTEM echo "processing_report.sql";
source ~/eb-fa/src/eulap/eb/conf/db/processing/processing_report.sql

SYSTEM echo "insert_processing_report_type_08_19_2015_15_45.sql";
source ~/eb-fa/src/eulap/eb/conf/db/data/insert_processing_report_type_08_19_2015_15_45.sql

SYSTEM echo "update_processing_report_add_processing_type_08_19_2015_16_12.sql";
source ~/eb-fa/src/eulap/eb/conf/db/update/update_processing_report_add_processing_type_08_19_2015_16_12.sql