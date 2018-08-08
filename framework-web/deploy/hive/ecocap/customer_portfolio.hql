CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
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
LOAD DATA LOCAL INPATH '/user/hive/warehouse/framework/upload/customer_portfolio_2018.csv' OVERWRITE INTO TABLE ecocap.customer_portfolio;





