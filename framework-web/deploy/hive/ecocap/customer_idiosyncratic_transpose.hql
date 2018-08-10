CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
alter table customer_idiosyncratic_transpose set tblproperties('EXTERNAL'='FALSE');
DROP TABLE IF EXISTS customer_idiosyncratic_transpose;
CREATE  TABLE IF NOT EXISTS `customer_idiosyncratic_transpose`(
  `iterationid` int, 
  `reporting_date` string,
  `customer` string, 
  `pd` double, 
  `version` string);