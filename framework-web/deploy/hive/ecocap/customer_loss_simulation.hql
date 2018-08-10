CREATE DATABASE IF NOT EXISTS ecocap;
use ecocap;

alter table customer_loss_simulation set tblproperties('EXTERNAL'='FALSE');
DROP TABLE IF EXISTS customer_loss_simulation;

CREATE  TABLE IF NOT EXISTS `customer_loss_simulation`(
  `cust_id` string, 
  `iterationid` int, 
  `customer_loss` decimal, 
  `reporting_date` string,
  `version` int);