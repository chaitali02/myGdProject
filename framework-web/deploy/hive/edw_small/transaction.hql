CREATE DATABASE IF NOT EXISTS edw_small;
use edw_small;
DROP TABLE IF EXISTS transaction;
CREATE TABLE IF NOT EXISTS `transaction`(
  `transaction_id` string, 
  `transaction_type_id` int, 
  `account_id` string, 
  `transaction_date` string, 
  `from_account` string, 
  `to_account` string, 
  `amount_base_curr` decimal(30,2), 
  `amount_usd` decimal(30,2), 
  `currency_code` string, 
  `currency_rate` float, 
  `notes` string)
PARTITIONED BY (
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE edw_small.transaction ADD PARTITION (load_date='2017-07-01', load_id='00');