CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS fact_transaction;
CREATE  TABLE IF NOT EXISTS `fact_transaction`(
  `transaction_id` string,
  `src_transaction_id` string,
  `transaction_type_id` string, 
  `trans_date_id` int, 
  `bank_id` string, 
  `branch_id` string, 
  `customer_id` string, 
  `address_id` string, 
  `account_id` string, 
  `from_account` string, 
  `to_account` string, 
  `amount_base_curr` decimal(30,2), 
  `amount_usd` decimal(30,2), 
  `currency_code` string, 
  `currency_rate` float, 
  `notes` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` bigint)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
ALTER TABLE framework.fact_transaction ADD PARTITION(load_date='2017-07-01', load_id=00);
