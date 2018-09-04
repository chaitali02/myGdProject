CREATE DATABASE IF NOT EXISTS edw_medium;
use edw_medium ;
DROP TABLE IF EXISTS branch_type;
CREATE TABLE IF NOT EXISTS `branch_type`(
  `branch_type_id` string, 
  `branch_type_code` string, 
  `branch_type_desc` string)
PARTITIONED BY (
  `load_date` string,
  `load_id` bigint)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE edw_medium.branch_type ADD PARTITION (load_date='2017-07-01', load_id='00');
