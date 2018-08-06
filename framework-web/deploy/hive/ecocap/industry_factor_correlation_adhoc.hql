CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS industry_factor_correlation_adhoc;
CREATE EXTERNAL TABLE IF NOT EXISTS `industry_factor_correlation_adhoc`(
  `factor` string, 
  `factor1` double, 
  `factor2` double, 
  `factor3` double, 
  `factor4` double,
  `version` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/industry_factor_correlation_adhoc.csv';


