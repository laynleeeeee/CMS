
-- Description	: Update script for November 09, 2021

-- Create loan proceeds type
source ~/CMS/src/eulap/eb/conf/db/revenue/loan_proceeds_type.sql
-- Insert default loan proceeds type
source ~/CMS/src/eulap/eb/conf/db/nsb/data/insert_nsb_default_loan_proceeds_type_10_13_2021_14_37.sql
-- Update loan proceeds add loan type id
source ~/CMS/src/eulap/eb/conf/db/nsb/data/update_loan_proceeds_add_type_id_10_13_2021_14_39.sql