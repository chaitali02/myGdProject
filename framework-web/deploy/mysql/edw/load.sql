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

LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/edw/data/csv/header/account.csv' IGNORE INTO TABLE ACCOUNT FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/edw/data/csv/header/account_status_type.csv' IGNORE INTO TABLE ACCOUNT_STATUS_TYPE FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/edw/data/csv/header/account_type.csv' IGNORE INTO TABLE ACCOUNT_TYPE FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/edw/data/csv/header/address.csv' IGNORE INTO TABLE ADDRESS FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/edw/data/csv/header/bank.csv' IGNORE INTO TABLE BANK FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/edw/data/csv/header/branch.csv' IGNORE INTO TABLE BRANCH FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/edw/data/csv/header/branch_type.csv' IGNORE INTO TABLE BRANCH_TYPE FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/edw/data/csv/header/customer.csv' IGNORE INTO TABLE CUSTOMER FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/edw/data/csv/header/dim_date.csv' IGNORE INTO TABLE DIM_DATE FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/edw/data/csv/header/product_type.csv' IGNORE INTO TABLE PRODUCT_TYPE FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/edw/data/csv/header/transaction.csv' IGNORE INTO TABLE TRANSACTION FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/app/edw/data/csv/header/transaction_type.csv' IGNORE INTO TABLE TRANSACTION_TYPE FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
