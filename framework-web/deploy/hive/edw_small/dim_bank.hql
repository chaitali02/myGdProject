CREATE DATABASE IF NOT EXISTS edw_small ;
use edw_small ;
DROP TABLE IF EXISTS dim_bank;
CREATE TABLE IF NOT EXISTS `dim_bank`(
  `bank_id` string,
  `src_bank_id` string,
  `bank_code` string, 
  `bank_name` string, 
  `bank_account_number` string, 
  `bank_currency_code` string, 
  `bank_check_digits` int)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
ALTER TABLE edw_small .dim_bank ADD PARTITION(load_date='2017-12-04', load_id='00');

