CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS dim_bank;
CREATE  TABLE IF NOT EXISTS `dim_bank`(
  `bank_id` string,
  `src_bank_id` string,
  `bank_code` string, 
  `bank_name` string, 
  `bank_account_number` string, 
  `bank_currency_code` string, 
  `bank_check_digits` int)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` bigint)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
ALTER TABLE framework.dim_bank ADD PARTITION(load_date='2017-07-01', load_id=00);
