
-- Description	: Consolidated script that will update saved AR invoices to fit the update from 5696 (Multiple DR to AR invoice update)

source ~/CMS/src/eulap/eb/conf/db/update/update_ar_invoice_set_dr_reference_ids_08_30_2022_14_27.sql

source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_sales_output.sql

source ~/CMS/src/eulap/eb/conf/db/sp/sp_get_sales_report.sql