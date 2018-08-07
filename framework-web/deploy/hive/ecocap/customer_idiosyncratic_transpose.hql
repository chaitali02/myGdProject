CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_idiosyncratic_transpose;
CREATE EXTERNAL TABLE IF NOT EXISTS `customer_idiosyncratic_transpose`(
  `iterationid` int, 
  `reporting_date` string,
  `customer` string, 
  `pd` double, 
  `version` string);
  