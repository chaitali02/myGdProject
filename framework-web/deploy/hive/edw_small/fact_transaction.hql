CREATE DATABASE IF NOT EXISTS edw_small;
use edw_small;
DROP TABLE IF EXISTS fact_transaction;
CREATE  TABLE IF NOT EXISTS `fact_transaction`(
  `transaction_id` string,
  `src_transaction_id` string,
  `transaction_type_id` string, 
  `trans_date_id` string, 
  `bank_id` string, 
  `branch_id` string, 
  `customer_id` string, 
  `address_id` string, 
  `account_id` string, 
  `from_account` string, 
  `to_account` string, 
  `amount_base_curr` int, 
  `amount_usd` int, 
  `currency_code` string, 
  `currency_rate` bigint, 
  `notes` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
ALTER TABLE edw_small.fact_transaction ADD PARTITION(load_date='2017-12-04', load_id='00');

