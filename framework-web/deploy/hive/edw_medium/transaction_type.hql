CREATE DATABASE IF NOT EXISTS edw_medium;
use edw_medium;
DROP TABLE IF EXISTS transaction_type;
CREATE TABLE IF NOT EXISTS `transaction_type`(
  `transaction_type_id` int, 
  `transaction_type_code` string, 
  `transaction_type_desc` string)
PARTITIONED BY (
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE edw_medium.transaction_type ADD PARTITION (load_date='2017-07-01', load_id='00');
