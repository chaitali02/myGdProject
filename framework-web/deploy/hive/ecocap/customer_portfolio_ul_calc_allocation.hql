CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_portfolio_ul_calc_allocation;
CREATE EXTERNAL TABLE IF NOT EXISTS `customer_portfolio_ul_calc_allocation`(
  `cust_id` string, 
  `portfolio_ul_cust_allocation` double, 
  `reporting_date` string,
  `version` int)
   PARTITIONED BY ( 
  `load_date` string,
  `load_id` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.customer_portfolio_ul_calc_allocation ADD PARTITION(load_date='2017-07-01', load_id='00');


