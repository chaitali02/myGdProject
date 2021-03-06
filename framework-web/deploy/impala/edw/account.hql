CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS account;
CREATE EXTERNAL TABLE IF NOT EXISTS `account`(
  `account_id` string, 
  `account_type_id` int, 
  `account_status_id` int, 
  `product_type_id` int, 
  `customer_id` string, 
  `pin_number` int, 
  `nationality` string, 
  `primary_iden_doc` string, 
  `primary_iden_doc_id` string, 
  `secondary_iden_doc` string, 
  `secondary_iden_doc_id` string, 
  `account_open_date` string, 
  `account_number` string, 
  `opening_balance` string, 
  `current_balance` string, 
  `overdue_balance` int, 
  `overdue_date` string, 
  `currency_code` string, 
  `interest_type` string, 
  `interest_rate` float)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` bigint)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.account ADD PARTITION(load_date='2017-12-04', load_id=00);
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/account.csv' OVERWRITE INTO TABLE account PARTITION (load_date='2017-07-01', load_id=00);



