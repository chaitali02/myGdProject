CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;
alter table customer_idiosyncratic_transpose_stage set tblproperties('EXTERNAL'='FALSE');
DROP TABLE IF EXISTS customer_idiosyncratic_transpose_stage;
CREATE  TABLE IF NOT EXISTS `customer_idiosyncratic_transpose_stage`(
  `iterationid` int, 
  `customer` string, 
  `pd` double, 
  `version` int);
  