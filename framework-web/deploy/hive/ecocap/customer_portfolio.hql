CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_portfolio;
CREATE EXTERNAL TABLE IF NOT EXISTS `customer_portfolio`(
  `cust_id` string, 
  `industry` string, 
  `pd` double, 
  `exposure` int, 
  `lgd` double, 
  `lgd_var` int, 
  `correlation` double, 
  `sqrt_correlation` double, 
  `def_point` double, 
  `reporting_date` string, 
  `version` int)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
TBLPROPERTIES ("skip.header.line.count"="1");
ALTER TABLE framework.customer_portfolio ADD PARTITION(load_date='2017-07-01', load_id='00');
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/customer_portfolio_1000.csv' OVERWRITE INTO TABLE customer_portfolio PARTITION (load_date='2017-07-01', load_id='00');



