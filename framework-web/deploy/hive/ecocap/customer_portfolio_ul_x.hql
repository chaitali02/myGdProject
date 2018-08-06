CREATE DATABASE IF NOT EXISTS framework;
use framework;
DROP TABLE IF EXISTS customer_portfolio_ul_x;
CREATE EXTERNAL TABLE IF NOT EXISTS `customer_portfolio_ul_x`(
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
  `version` int;


