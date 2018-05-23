CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS bank;
CREATE EXTERNAL TABLE IF NOT EXISTS `bank`(
  `bank_id` string, 
  `bank_code` string, 
  `bank_name` string, 
  `bank_account_number` string, 
  `bank_currency_code` string, 
  `bank_check_digits` int)
PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.bank ADD PARTITION (load_date='2017-07-01', load_id='00');
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/bank.csv' OVERWRITE INTO TABLE framework.bank PARTITION (load_date='2017-07-01', load_id='00'); 

