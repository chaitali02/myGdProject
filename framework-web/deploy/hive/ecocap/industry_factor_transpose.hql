CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS industry_factor_transpose;
CREATE EXTERNAL TABLE IF NOT EXISTS `industry_factor_transpose`(
  `iteration_id` int, 
  `factor` string, 
  `factor_value` double,
  `version` string)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.industry_factor_transpose ADD PARTITION(load_date='2017-07-01', load_id='00');
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/industry_factor_transpose.csv' OVERWRITE INTO TABLE industry_factor_transpose PARTITION (load_date='2017-07-01', load_id='00');


