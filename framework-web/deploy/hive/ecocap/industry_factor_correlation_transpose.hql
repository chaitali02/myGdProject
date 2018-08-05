CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS industry_factor_correlation_transpose;
CREATE EXTERNAL TABLE IF NOT EXISTS `industry_factor_correlation_transpose`(
  `factor_x` string, 
  `factor_y` string, 
  `factor_value` double,
  `reporting_date` string,  
  `version` int)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.industry_factor_correlation_transpose ADD PARTITION(load_date='2017-07-01', load_id='00');
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/industry_factor_correlation_transpose.csv' OVERWRITE INTO TABLE industry_factor_correlation_transpose PARTITION (load_date='2017-07-01', load_id='00');


