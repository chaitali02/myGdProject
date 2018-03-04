CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS dim_account;
CREATE TABLE IF NOT EXISTS `dim_account`(
  `account_id` string, 
  `src_account_id` string,
  `account_type_code` string, 
  `account_status_code` string, 
  `product_type_code` string, 
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
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
ALTER TABLE framework.dim_account ADD PARTITION(load_date='2017-07-01', load_id=00);
