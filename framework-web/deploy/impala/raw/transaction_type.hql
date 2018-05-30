CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS transaction_type;
CREATE EXTERNAL TABLE IF NOT EXISTS `transaction_type`(
  `transaction_type_id` int, 
  `transaction_type_code` string, 
  `transaction_type_desc` string)
PARTITIONED BY (
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.transaction_type ADD PARTITION (load_date='2017-07-01', load_id='00');
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/transaction_type.csv' OVERWRITE INTO TABLE framework.transaction_type PARTITION (load_date='2017-07-01', load_id='00');
