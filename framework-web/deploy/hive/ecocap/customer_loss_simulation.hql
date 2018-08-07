CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_loss_simulation;
CREATE EXTERNAL TABLE IF NOT EXISTS `customer_loss_simulation`(
  `cust_id` string, 
  `iterationid` int, 
  `customer_loss` decimal, 
  `reporting_date` string,
  `version` int);



