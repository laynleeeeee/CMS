
-- Description	: Consolidated script to add final tax withheld procedures

source ~/CMS/src/eulap/eb/conf/db/data/insert_atc_default_data_01_26_2022_11_05.sql

source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_final_tax_withheld.sql

source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_final_tax_withheld_goods.sql

source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_final_tax_withheld_services.sql

