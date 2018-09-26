CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS account_status_type;
CREATE EXTERNAL TABLE IF NOT EXISTS `account_status_type`(
  `account_status_id` int, 
  `account_status_code` string, 
  `account_status_desc` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.account_status_type ADD PARTITION(load_date='2017-07-01', load_id='00');
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/account_status_type.csv' OVERWRITE INTO TABLE framework.account_status_type PARTITION (load_date='2017-07-01', load_id='00');

  
