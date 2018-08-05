CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_portfolio_ul_calc;
CREATE EXTERNAL TABLE IF NOT EXISTS `customer_portfolio_ul_calc`(
  `cust_id1` string, 
  `industry1` string, 
  `correlation1` double, 
  `unexpected_loss1` double,
  `cust_id2` double, 
  `industry2` string, 
  `correlation2` double, 
  `unexpected_loss2` double, 
  `factor_value` double, 
  `portfolio_ul_calc` double, 
  `reporting_date` string, 
  `version` int)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.customer_portfolio_ul_calc ADD PARTITION(load_date='2017-07-01', load_id='00');


