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
TRUNCATE TABLE RC_RULE_RESULTS;

Copy ACCOUNT FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account.csv' With csv delimiter ',';

Copy ACCOUNT FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account.csv' With csv delimiter ',';

Copy ACCOUNT_STATUS_TYPE FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account_status_type.csv' With csv delimiter ',';

Copy ACCOUNT_TYPE FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/account_type.csv' With csv delimiter ',';

Copy ADDRESS FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/address.csv' With csv delimiter ',';

Copy BANK FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/bank.csv' With csv delimiter ',';

Copy BRANCH FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/branch.csv' With csv delimiter ',';

Copy BRANCH_TYPE FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/branch_type.csv' With csv delimiter ',';

Copy CUSTOMER FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/customer.csv' With csv delimiter ',';

Copy DIM_DATE FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/dim_date.csv' With csv delimiter ',';

Copy PRODUCT_TYPE FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/product_type.csv' With csv delimiter ',';

Copy TRANSACTION FROM '/user/hive/warehouse/framework/app/edw/data/csv/noheader/transaction.csv' With csv delimiter ',';



