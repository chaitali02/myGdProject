TRUNCATE TABLE DIM_ACCOUNT;
TRUNCATE TABLE DIM_ADDRESS;
TRUNCATE TABLE DIM_BANK;
TRUNCATE TABLE DIM_BRANCH; 
TRUNCATE TABLE DIM_COUNTRY;
TRUNCATE TABLE DIM_CUSTOMER;
TRUNCATE TABLE DIM_STATE;
TRUNCATE TABLE DIM_TRANSACTION_TYPE;
TRUNCATE TABLE DP_RULE_RESULTS;
TRUNCATE TABLE DQ_RULE_RESULTS;
TRUNCATE TABLE FACT_ACCOUNT_SUMMARY_MONTHLY;
TRUNCATE TABLE FACT_CUSTOMER_SUMMARY_MONTHLY;
TRUNCATE TABLE FACT_TRANSACTION;
TRUNCATE TABLE PROFILE_RULE_RESULTS;
TRUNCATE TABLE RC_RULE_RESULTS;


Copy ACCOUNT(account_id,account_type_id,account_status_id,product_type_id,customer_id,pin_number,nationality,primary_iden_doc,primary_iden_doc_id,secondary_iden_doc,secondary_iden_doc_id,account_open_date,account_number,opening_balance,current_balance,overdue_balance,overdue_date,currency_code,interest_type,interest_rate,load_date, load_id) FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account.csv' With csv delimiter ',';

Copy ACCOUNT_STATUS_TYPE(account_status_id,account_status_code,account_status_desc,load_date, load_id)FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account_status_type.csv' With csv delimiter ',';

Copy ACCOUNT_TYPE(account_type_id,account_type_code,account_type_desc,load_date, load_id)FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account_type.csv' With csv delimiter ',';

Copy ADDRESS(address_id,address_line1,address_line2,address_line3,city,county,state,zipcode,country,latitude,longitude,load_date, load_id)FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/address.csv' With csv delimiter ',';

Copy BANK(bank_id,bank_code,bank_name,bank_account_number,bank_currency_code,bank_check_digits,load_date, load_id)FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/bank.csv' With csv delimiter ',';

Copy BRANCH(branch_id,branch_type_id,bank_id,address_id,branch_name,branch_desc,branch_contact_name,branch_contact_phone,branch_contact_email,load_date, load_id)FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/branch.csv' With csv delimiter ',';

Copy BRANCH_TYPE(branch_type_id,branch_type_code,branch_type_desc,load_date, load_id)FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/branch_type.csv' With csv delimiter ',';

Copy CUSTOMER(customer_id,address_id,branch_id,title,first_name,middle_name,last_name,ssn,phone,date_first_purchase,commute_distance_miles,load_date, load_id)FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/customer.csv' With csv delimiter ',';

Copy DIM_DATE(date_id,date_type,date_val,day_num_of_week,day_num_of_month,day_num_of_quarter,day_num_of_year,day_num_absolute,day_of_week_name,day_of_week_abbreviation,julian_day_num_of_year,julian_day_num_absolute,is_weekday,is_usa_civil_holiday,is_last_day_of_week,is_last_day_of_month,is_last_day_of_quarter,is_last_day_of_year,is_last_day_of_fiscal_month,is_last_day_of_fiscal_quarter,is_last_day_of_fiscal_year,week_of_year_begin_date,week_of_year_begin_date_key,week_of_year_end_date,week_of_year_end_date_key,week_of_month_begin_date,week_of_month_begin_date_key,week_of_month_end_date,week_of_month_end_date_key,week_of_quarter_begin_date,week_of_quarter_begin_date_key,week_of_quarter_end_date,week_of_quarter_end_date_key,week_num_of_month,week_num_of_quarter,week_num_of_year,month_num_of_year,month_num_overall,month_name,month_name_abbreviation,month_begin_date,month_begin_date_key,month_end_date,month_end_date_key,quarter_num_of_year,quarter_num_overall,quarter_begin_date,quarter_begin_date_key,quarter_end_date,quarter_end_date_key,year_num,year_begin_date,year_begin_date_key,year_end_date,year_end_date_key,yyyy_mm,yyyy_mm_dd,dd_mon_yyyy,load_date)FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/dim_date.csv' With csv delimiter ',';

Copy PRODUCT_TYPE(product_type_id,product_type_code,product_type_desc,load_date, load_id)FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/product_type.csv' With csv delimiter ',';

Copy TRANSACTION(transaction_id,transaction_type_id,account_id,transaction_date,from_account,to_account,amount_base_curr,amount_usd,currency_code,currency_rate,notes,load_date, load_id)FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/transaction.csv' With csv delimiter ',';

Copy TRANSACTION_TYPE(transaction_type_id,transaction_type_code,transaction_type_desc,load_date, load_id)FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/transaction_type.csv' With csv delimiter ',';


