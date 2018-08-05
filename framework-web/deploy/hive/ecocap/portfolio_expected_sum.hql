CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS portfolio_expected_sum;
CREATE EXTERNAL TABLE IF NOT EXISTS `portfolio_expected_sum`(
  `expected_sum` decimal)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.portfolio_expected_sum ADD PARTITION(load_date='2017-07-01', load_id='00');


