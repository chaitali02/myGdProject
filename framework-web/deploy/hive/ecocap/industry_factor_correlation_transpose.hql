CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS industry_factor_correlation_transpose;
CREATE EXTERNAL TABLE IF NOT EXISTS `industry_factor_correlation_transpose`(
  `factor_x` string, 
  `reporting_date` string, 
  `factor_y` string, 
  `factor_value` double, 
  `version` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/industry_factor_correlation_transpose.csv';



