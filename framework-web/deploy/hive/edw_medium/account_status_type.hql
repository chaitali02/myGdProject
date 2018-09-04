CREATE DATABASE IF NOT EXISTS edw_medium;
use edw_medium;
DROP TABLE IF EXISTS account_status_type;
CREATE TABLE IF NOT EXISTS `account_status_type`(
  `account_status_id` int, 
  `account_status_code` string, 
  `account_status_desc` string)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE edw_medium.account_status_type ADD PARTITION(load_date='2017-07-01', load_id='00');

  
