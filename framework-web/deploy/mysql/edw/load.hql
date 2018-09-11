truncate table dim_account;
truncate table dim_address;
truncate table dim_bank;
truncate table dim_branch; 
truncate table dim_country;
truncate table dim_customer;
truncate table dim_state;
truncate table dim_transaction_type;
truncate table dp_rule_results;
truncate table dq_rule_results;
truncate table fact_account_summary_monthly;
truncate table fact_customer_summary_monthly;
truncate table fact_transaction;
truncate table profile_rule_results;
truncate table rc_rule_results;



LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/account.csv' IGNORE INTO TABLE account FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/account_status_type.csv' IGNORE INTO TABLE account_status_type FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/account_type.csv' IGNORE INTO TABLE account_type FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/address.csv' IGNORE INTO TABLE address FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/bank.csv' IGNORE INTO TABLE bank FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/branch.csv' IGNORE INTO TABLE branch FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/branch_type.csv' IGNORE INTO TABLE branch_type FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

LOAD DATA LOCAL INFILE '/user/hive/warehouse/framework/upload/customer.csv' IGNORE INTO TABLE customer FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/dim_date.csv' IGNORE INTO TABLE dim_date FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/product_type.csv' IGNORE INTO TABLE product_type FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/transaction.csv' IGNORE INTO TABLE transaction FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;

LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/transaction_type.csv' IGNORE INTO TABLE transaction_type FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\n' IGNORE 1 LINES;
