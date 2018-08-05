CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_portfolio_ul;
CREATE EXTERNAL TABLE IF NOT EXISTS `customer_portfolio_ul`(
  `cust_id` string, 
  `industry` string, 
  `pd` double, 
  `exposure` int,
  `lgd` double, 
  `lgd_var` int, 
  `correlation` double, 
  `sqrt_correlation` double, 
  `def_point` double, 
  `unexpected_loss` double, 
  `reporting_date` string, 
  `version` int)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.customer_portfolio_ul ADD PARTITION(load_date='2017-07-01', load_id='00');


