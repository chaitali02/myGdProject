CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
DROP TABLE IF EXISTS industry_factor_correlation;
CREATE TABLE IF NOT EXISTS `industry_factor_correlation`(
  `factor` string, 
  `factor1` double, 
  `factor2` double, 
  `factor3` double, 
  `factor4` double, 
  `reporting_date` string,
  `version` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");