
-- Description : Consolidated script for direct payment module.

System echo "update_ap_payment_06_05_2020_22_26";
source ~/CMS/src/eulap/eb/conf/db/directpayment/update_ap_payment_06_05_2020_22_26.sql

System echo "payment_type";
source ~/CMS/src/eulap/eb/conf/db/directpayment/direct_payment_type.sql

System echo "direct_payment";
source ~/CMS/src/eulap/eb/conf/db/directpayment/direct_payment.sql

System echo "direct_payment_line";
source ~/CMS/src/eulap/eb/conf/db/directpayment/direct_payment_line.sql

System echo "insert_payment_type";
source ~/CMS/src/eulap/eb/conf/db/directpayment/insert_direct_payment_type.sql

System echo "insert_or_type_direct_payment";
source ~/CMS/src/eulap/eb/conf/db/directpayment/insert_object_relationship_direct_payment.sql