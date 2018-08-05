CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS portfolio_loss_aggr;
CREATE EXTERNAL TABLE IF NOT EXISTS `portfolio_loss_aggr`(
  `expected_loss` decimal, 
  `value_at_risk` decimal, 
  `economic_capital` decimal)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.portfolio_loss_aggr ADD PARTITION(load_date='2017-07-01', load_id='00');


