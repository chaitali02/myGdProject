CREATE DATABASE IF NOT EXISTS framework;
use framework ;
DROP TABLE IF EXISTS branch_type;
CREATE EXTERNAL TABLE IF NOT EXISTS `branch_type`(
  `branch_type_id` string, 
  `branch_type_code` string, 
  `branch_type_desc` string)
PARTITIONED BY (
  `load_date` string,
  `load_id` bigint)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.branch_type ADD PARTITION (load_date='2017-07-01', load_id='00');
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/branch_type.csv' OVERWRITE INTO TABLE framework.branch_type PARTITION (load_date='2017-07-01', load_id='00'); 

